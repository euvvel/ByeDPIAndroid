package io.github.dovecoteescapee.byedpi.core

import android.util.Log

class ByeDpiProxy {
    
    companion object {
        private const val TAG = "ByeDpiProxy"
        
        init {
            System.loadLibrary("byedpi")
        }
    }
    
    private var isRunning = false
    
    fun startProxy(preferences: ByeDpiProxyPreferences): Int {
        Log.d(TAG, "Starting proxy")
        
        if (isRunning) {
            Log.w(TAG, "Proxy already running")
            return -1
        }
        
        return try {
            val result = createSocketFromPreferences(preferences)
            if (result == 0) {
                isRunning = true
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start proxy", e)
            -1
        }
    }
    
    fun stopProxy() {
        Log.d(TAG, "Stopping proxy")
        if (isRunning) {
            jniStopProxy()
            isRunning = false
        }
    }
    
    private fun createSocketFromPreferences(preferences: ByeDpiProxyPreferences): Int =
        when (preferences) {
            is ByeDpiProxyCmdPreferences -> {
                jniCreateSocketWithCommandLine(preferences.toCommandLineArguments())
            }
            
            is ByeDpiProxyUIPreferences -> {
                jniCreateSocket(
                    preferences.ip,
                    preferences.port,
                    preferences.maxConnections,
                    preferences.bufferSize,
                    preferences.defaultTtl,
                    preferences.customTtl,
                    preferences.noDomain,
                    preferences.desyncHttp,
                    preferences.desyncHttps,
                    preferences.desyncUdp,
                    preferences.desyncMethod.ordinal,
                    preferences.splitPosition,
                    preferences.splitAtHost,
                    preferences.fakeTtl,
                    preferences.fakeSni,
                    preferences.oobChar,
                    preferences.hostMixedCase,
                    preferences.domainMixedCase,
                    preferences.hostRemoveSpaces,
                    preferences.tlsRecordSplit,
                    preferences.tlsRecordSplitPosition,
                    preferences.tlsRecordSplitAtSni,
                    preferences.hostsMode.ordinal,
                    preferences.hosts,
                    preferences.tcpFastOpen,
                    preferences.udpFakeCount,
                    preferences.dropSack,
                    preferences.fakeOffset,
                )
            }
            
            else -> {
                throw IllegalArgumentException("Unknown preferences type: ${preferences.javaClass.name}")
            }
        }
    
    private external fun jniCreateSocket(
        ip: String,
        port: Int,
        maxConnections: Int,
        bufferSize: Int,
        defaultTtl: Int,
        customTtl: Int,
        noDomain: Boolean,
        desyncHttp: Boolean,
        desyncHttps: Boolean,
        desyncUdp: Boolean,
        desyncMethod: Int,
        splitPosition: Int,
        splitAtHost: Boolean,
        fakeTtl: Int,
        fakeSni: String,
        oobChar: Char,
        hostMixedCase: Boolean,
        domainMixedCase: Boolean,
        hostRemoveSpaces: Boolean,
        tlsRecordSplit: Boolean,
        tlsRecordSplitPosition: Int,
        tlsRecordSplitAtSni: Boolean,
        hostsMode: Int,
        hosts: String,
        tcpFastOpen: Boolean,
        udpFakeCount: Int,
        dropSack: Boolean,
        fakeOffset: Int,
    ): Int
    
    private external fun jniCreateSocketWithCommandLine(args: Array<String>): Int
    private external fun jniStopProxy()
}