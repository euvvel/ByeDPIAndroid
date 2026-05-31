package io.github.dovecoteescapee.byedpi.core

import android.content.SharedPreferences

class ByeDpiProxyUIPreferences(
    val ip: String = "188.114.98.0",
    val port: Int = 443,
    val maxConnections: Int = 512,
    val bufferSize: Int = 16384,
    val defaultTtl: Int = 64,
    val customTtl: Int = 0,
    val noDomain: Boolean = false,
    val desyncHttp: Boolean = true,
    val desyncHttps: Boolean = true,
    val desyncUdp: Boolean = false,
    val desyncMethod: DesyncMethod = DesyncMethod.Fake,
    val splitPosition: Int = 2,
    val splitAtHost: Boolean = false,
    val fakeTtl: Int = 0,
    val fakeSni: String = "auth.vercel.com",
    val oobChar: Char = ' ',
    val hostMixedCase: Boolean = false,
    val domainMixedCase: Boolean = false,
    val hostRemoveSpaces: Boolean = false,
    val tlsRecordSplit: Boolean = true,
    val tlsRecordSplitPosition: Int = 2,
    val tlsRecordSplitAtSni: Boolean = true,
    val hostsMode: HostsMode = HostsMode.None,
    val hosts: String = "",
    val tcpFastOpen: Boolean = false,
    val udpFakeCount: Int = 0,
    val dropSack: Boolean = false,
    val fakeOffset: Int = 0,
) : ByeDpiProxyPreferences {

    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences): ByeDpiProxyUIPreferences {
            return ByeDpiProxyUIPreferences(
                ip = prefs.getString("connect_ip", "188.114.98.0") ?: "188.114.98.0",
                port = prefs.getString("connect_port", "443")?.toIntOrNull() ?: 443,
                maxConnections = prefs.getString("max_connections", "512")?.toIntOrNull() ?: 512,
                bufferSize = prefs.getString("buffer_size", "16384")?.toIntOrNull() ?: 16384,
                defaultTtl = prefs.getString("default_ttl", "64")?.toIntOrNull() ?: 64,
                customTtl = prefs.getString("custom_ttl", "0")?.toIntOrNull() ?: 0,
                noDomain = prefs.getBoolean("no_domain", false),
                desyncHttp = prefs.getBoolean("desync_http", true),
                desyncHttps = prefs.getBoolean("desync_https", true),
                desyncUdp = prefs.getBoolean("desync_udp", false),
                desyncMethod = try {
                    DesyncMethod.valueOf(prefs.getString("desync_method", "Fake") ?: "Fake")
                } catch (e: IllegalArgumentException) {
                    DesyncMethod.Fake
                },
                splitPosition = prefs.getString("split_position", "2")?.toIntOrNull() ?: 2,
                splitAtHost = prefs.getBoolean("split_at_host", false),
                fakeTtl = prefs.getString("fake_ttl", "0")?.toIntOrNull() ?: 0,
                fakeSni = prefs.getString("fake_sni", "auth.vercel.com") ?: "auth.vercel.com",
                oobChar = prefs.getString("oob_char", " ")?.firstOrNull() ?: ' ',
                hostMixedCase = prefs.getBoolean("host_mixed_case", false),
                domainMixedCase = prefs.getBoolean("domain_mixed_case", false),
                hostRemoveSpaces = prefs.getBoolean("host_remove_spaces", false),
                tlsRecordSplit = prefs.getBoolean("tls_record_split", true),
                tlsRecordSplitPosition = prefs.getString("tls_record_split_position", "2")?.toIntOrNull() ?: 2,
                tlsRecordSplitAtSni = prefs.getBoolean("tls_record_split_at_sni", true),
                hostsMode = try {
                    HostsMode.valueOf(prefs.getString("hosts_mode", "None") ?: "None")
                } catch (e: IllegalArgumentException) {
                    HostsMode.None
                },
                hosts = prefs.getString("hosts", "") ?: "",
                tcpFastOpen = prefs.getBoolean("tcp_fast_open", false),
                udpFakeCount = prefs.getString("udp_fake_count", "0")?.toIntOrNull() ?: 0,
                dropSack = prefs.getBoolean("drop_sack", false),
                fakeOffset = prefs.getString("fake_offset", "0")?.toIntOrNull() ?: 0,
            )
        }
    }

    override fun toCommandLineArguments(): Array<String> {
        val args = mutableListOf<String>()
        args.add("ciadpi")
        args.add("--ip")
        args.add(ip)
        args.add("--port")
        args.add(port.toString())
        
        if (desyncHttp) args.add("--disorder")
        if (desyncHttps) {
            args.add("--fake")
            args.add("-1")
            args.add("--split")
            args.add("$splitPosition+s")
        }
        
        args.add("--fake-sni")
        args.add(fakeSni)
        
        if (fakeTtl > 0) {
            args.add("--auto-ttl")
            args.add("--ttl")
            args.add(fakeTtl.toString())
        }
        
        if (tlsRecordSplit) {
            args.add("--tlsrec")
            args.add("$tlsRecordSplitPosition+s")
        }
        
        when (hostsMode) {
            HostsMode.Blacklist -> args.add("--hosts")
            HostsMode.Whitelist -> args.add("--whitelist")
            HostsMode.None -> {}
        }
        
        return args.toTypedArray()
    }
}