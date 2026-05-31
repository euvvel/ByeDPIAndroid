package io.github.dovecoteescapee.byedpi.services

import android.app.Notification
import android.app.PendingIntent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ServiceLifecycleDispatcher

open class LifecycleVpnService : VpnService(), LifecycleOwner {
    
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val dispatcher = ServiceLifecycleDispatcher(this)
    
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    
    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }
    
    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }
    
    override fun onRevoke() {
        super.onRevoke()
    }
    
    open fun createBuilder(dns: String, ipv6: Boolean): Builder {
        return Builder(this)
    }
    
    class Builder(private val service: VpnService) {
        private val builder = VpnService.Builder()
        
        fun setSession(name: String): Builder {
            builder.setSession(name)
            return this
        }
        
        fun setConfigureIntent(intent: PendingIntent): Builder {
            builder.setConfigureIntent(intent)
            return this
        }
        
        fun addAddress(address: String, prefixLength: Int): Builder {
            builder.addAddress(address, prefixLength)
            return this
        }
        
        fun addRoute(address: String, prefixLength: Int): Builder {
            builder.addRoute(address, prefixLength)
            return this
        }
        
        fun addDnsServer(address: String): Builder {
            builder.addDnsServer(address)
            return this
        }
        
        fun setMetered(metered: Boolean): Builder {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                builder.setMetered(metered)
            }
            return this
        }
        
        fun addDisallowedApplication(packageName: String): Builder {
            builder.addDisallowedApplication(packageName)
            return this
        }
        
        fun establish(): ParcelFileDescriptor? {
            return try {
                builder.establish()
            } catch (e: Exception) {
                null
            }
        }
    }
}