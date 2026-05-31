package io.github.dovecoteescapee.byedpi.core

import android.util.Log

class TProxyService {
    
    companion object {
        private const val TAG = "TProxyService"
        
        init {
            try {
                System.loadLibrary("hev-socks5-tunnel")
                Log.d(TAG, "Native library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library: ${e.message}")
            }
        }
        
        @JvmStatic
        external fun TProxyStartService(configPath: String, fd: Int)
        
        @JvmStatic
        external fun TProxyStopService()
    }
}