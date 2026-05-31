package io.github.dovecoteescapee.byedpi.core

import android.content.SharedPreferences

class ByeDpiProxyUIPreferences(
    // Connection settings
    val ip: String = "188.114.98.0",
    val port: Int = 443,
    val listenPort: Int = 40443,
    val maxConnections: Int = 512,
    val bufferSize: Int = 16384,
    
    // TTL settings
    val defaultTtl: Int = 64,
    val customTtl: Int = 0,
    val fakeTtl: Int = 0,
    
    // Domain settings
    val noDomain: Boolean = false,
    val hostMixedCase: Boolean = false,
    val domainMixedCase: Boolean = false,
    val hostRemoveSpaces: Boolean = false,
    
    // Desync settings
    val desyncHttp: Boolean = true,
    val desyncHttps: Boolean = true,
    val desyncUdp: Boolean = false,
    val desyncMethod: DesyncMethod = DesyncMethod.Fake,
    
    // Split settings
    val splitPosition: Int = 2,
    val splitAtHost: Boolean = false,
    
    // Fake settings
    val fakeSni: String = "auth.vercel.com",
    val fakeOffset: Int = 0,
    val oobChar: Char = ' ',
    
    // TLS settings
    val tlsRecordSplit: Boolean = true,
    val tlsRecordSplitPosition: Int = 2,
    val tlsRecordSplitAtSni: Boolean = true,
    
    // Hosts settings
    val hostsMode: HostsMode = HostsMode.None,
    val hosts: String = "",
    
    // Advanced settings
    val tcpFastOpen: Boolean = false,
    val udpFakeCount: Int = 0,
    val dropSack: Boolean = false,
    
) : ByeDpiProxyPreferences {

    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences): ByeDpiProxyUIPreferences {
            return ByeDpiProxyUIPreferences(
                ip = prefs.getString("connect_ip", "188.114.98.0") ?: "188.114.98.0",
                port = prefs.getString("connect_port", "443")?.toIntOrNull() ?: 443,
                listenPort = prefs.getString("listen_port", "40443")?.toIntOrNull() ?: 40443,
                maxConnections = prefs.getString("max_connections", "512")?.toIntOrNull() ?: 512,
                bufferSize = prefs.getString("buffer_size", "16384")?.toIntOrNull() ?: 16384,
                
                defaultTtl = prefs.getString("default_ttl", "64")?.toIntOrNull() ?: 64,
                customTtl = prefs.getString("custom_ttl", "0")?.toIntOrNull() ?: 0,
                fakeTtl = prefs.getString("fake_ttl", "0")?.toIntOrNull() ?: 0,
                
                noDomain = prefs.getBoolean("no_domain", false),
                hostMixedCase = prefs.getBoolean("host_mixed_case", false),
                domainMixedCase = prefs.getBoolean("domain_mixed_case", false),
                hostRemoveSpaces = prefs.getBoolean("host_remove_spaces", false),
                
                desyncHttp = prefs.getBoolean("desync_http", true),
                desyncHttps = prefs.getBoolean("desync_https", true),
                desyncUdp = prefs.getBoolean("desync_udp", false),
                desyncMethod = DesyncMethod.valueOf(
                    prefs.getString("desync_method", "Fake") ?: "Fake"
                ),
                
                splitPosition = prefs.getString("split_position", "2")?.toIntOrNull() ?: 2,
                splitAtHost = prefs.getBoolean("split_at_host", false),
                
                fakeSni = prefs.getString("fake_sni", "auth.vercel.com") ?: "auth.vercel.com",
                fakeOffset = prefs.getString("fake_offset", "0")?.toIntOrNull() ?: 0,
                oobChar = prefs.getString("oob_char", " ")?.firstOrNull() ?: ' ',
                
                tlsRecordSplit = prefs.getBoolean("tls_record_split", true),
                tlsRecordSplitPosition = prefs.getString("tls_record_split_position", "2")?.toIntOrNull() ?: 2,
                tlsRecordSplitAtSni = prefs.getBoolean("tls_record_split_at_sni", true),
                
                hostsMode = HostsMode.valueOf(
                    prefs.getString("hosts_mode", "None") ?: "None"
                ),
                hosts = prefs.getString("hosts", "") ?: "",
                
                tcpFastOpen = prefs.getBoolean("tcp_fast_open", false),
                udpFakeCount = prefs.getString("udp_fake_count", "0")?.toIntOrNull() ?: 0,
                dropSack = prefs.getBoolean("drop_sack", false),
            )
        }
    }

    override fun toCommandLineArguments(): Array<String> {
        val args = mutableListOf<String>()

        args.add("ciadpi")
        
        // Connection settings
        args.add("--ip")
        args.add(ip)
        args.add("--port")
        args.add(port.toString())
        args.add("--lport")
        args.add(listenPort.toString())
        
        // Desync settings
        if (desyncHttp) args.add("--disorder")
        if (desyncHttps) {
            args.add("--fake")
            args.add("-1")
            args.add("--split")
            args.add("$splitPosition+s")
        }
        
        // Fake SNI
        args.add("--fake-sni")
        args.add(fakeSni)
        
        // TTL settings
        if (fakeTtl > 0) {
            args.add("--auto-ttl")
            args.add("--ttl")
            args.add(fakeTtl.toString())
        }
        
        // TLS record split
        if (tlsRecordSplit) {
            args.add("--tlsrec")
            args.add("$tlsRecordSplitPosition+s")
        }
        
        // Hosts mode
        when (hostsMode) {
            HostsMode.Blacklist -> args.add("--hosts")
            HostsMode.Whitelist -> args.add("--whitelist")
            HostsMode.None -> {}
        }
        
        return args.toTypedArray()
    }
}