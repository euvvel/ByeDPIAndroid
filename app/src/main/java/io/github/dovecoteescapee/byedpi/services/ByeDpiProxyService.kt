package io.github.dovecoteescapee.byedpi.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.activities.MainActivity
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxy
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxyPreferences
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxyCmdPreferences
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxyUIPreferences
import io.github.dovecoteescapee.byedpi.data.*
import io.github.dovecoteescapee.byedpi.utility.*
import kotlinx.coroutines.*

class ByeDpiProxyService : Service() {
    private val byeDpiProxy = ByeDpiProxy()
    private var proxyJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private val TAG: String = ByeDpiProxyService::class.java.simpleName
        private const val FOREGROUND_SERVICE_ID: Int = 2
        private const val NOTIFICATION_CHANNEL_ID: String = "ByeDPIProxy"
        
        private var status: ServiceStatus = ServiceStatus.Disconnected
    }
    
    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.proxy_channel_name,
        )
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (val action = intent?.action) {
            START_ACTION -> {
                scope.launch { start() }
                START_STICKY
            }
            STOP_ACTION -> {
                scope.launch { stop() }
                START_NOT_STICKY
            }
            else -> {
                Log.w(TAG, "Unknown action: $action")
                START_NOT_STICKY
            }
        }
    }
    
    private suspend fun start() {
        Log.i(TAG, "Starting proxy service")
        
        if (status == ServiceStatus.Connected) {
            Log.w(TAG, "Proxy already connected")
            return
        }
        
        try {
            startProxy()
            updateStatus(ServiceStatus.Connected)
            startForeground()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start proxy", e)
            updateStatus(ServiceStatus.Failed)
            stop()
        }
    }
    
    private fun startForeground() {
        val notification: Notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                FOREGROUND_SERVICE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(FOREGROUND_SERVICE_ID, notification)
        }
    }
    
    private suspend fun stop() {
        Log.i(TAG, "Stopping proxy service")
        
        try {
            stopProxy()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop proxy", e)
        }
        
        updateStatus(ServiceStatus.Disconnected)
        stopSelf()
    }
    
    private suspend fun startProxy() {
        Log.i(TAG, "Starting proxy")
        
        if (proxyJob != null) {
            Log.w(TAG, "Proxy already running")
            throw IllegalStateException("Proxy fields not null")
        }
        
        val preferences = getByeDpiPreferences()
        
        proxyJob = scope.launch {
            val code = byeDpiProxy.startProxy(preferences)
            
            if (code != 0) {
                Log.e(TAG, "Proxy stopped with code $code")
                updateStatus(ServiceStatus.Failed)
            } else {
                updateStatus(ServiceStatus.Disconnected)
            }
        }
        
        Log.i(TAG, "Proxy started")
    }
    
    private suspend fun stopProxy() {
        Log.i(TAG, "Stopping proxy")
        
        byeDpiProxy.stopProxy()
        proxyJob?.join() ?: throw IllegalStateException("ProxyJob field null")
        proxyJob = null
        
        Log.i(TAG, "Proxy stopped")
    }
    
    private fun getByeDpiPreferences(): ByeDpiProxyPreferences {
        val prefs = getPreferences()
        return if (prefs.getBoolean("use_ui_settings", true)) {
            ByeDpiProxyUIPreferences.fromSharedPreferences(prefs)
        } else {
            ByeDpiProxyCmdPreferences.fromSharedPreferences(prefs)
        }
    }
    
    private fun updateStatus(newStatus: ServiceStatus) {
        Log.d(TAG, "Proxy status changed from $status to $newStatus")
        status = newStatus
        
        setStatus(
            when (newStatus) {
                ServiceStatus.Connected -> AppStatus.Running
                ServiceStatus.Disconnected,
                ServiceStatus.Failed -> {
                    proxyJob = null
                    AppStatus.Halted
                }
            },
            Mode.PROXY
        )
        
        val intent = Intent(
            when (newStatus) {
                ServiceStatus.Connected -> STARTED_BROADCAST
                ServiceStatus.Disconnected -> STOPPED_BROADCAST
                ServiceStatus.Failed -> FAILED_BROADCAST
            }
        )
        intent.putExtra(SENDER, Sender.Proxy.ordinal)
        sendBroadcast(intent)
    }
    
    private fun createNotification(): Notification =
        createConnectionNotification(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_title,
            R.string.proxy_notification_content,
            ByeDpiProxyService::class.java,
        )
    
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}