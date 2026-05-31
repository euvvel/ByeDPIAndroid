package io.github.dovecoteescapee.byedpi.core

import android.content.SharedPreferences

class ByeDpiProxyUIPreferences(
    val fakeSni: String = "auth.vercel.com",
    val connectIp: String = "188.114.98.0",
    val connectPort: Int = 443,
    val listenPort: Int = 40443,
) : ByeDpiProxyPreferences {

    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences): ByeDpiProxyUIPreferences {
            return ByeDpiProxyUIPreferences(
                fakeSni = prefs.getString("fake_sni", "auth.vercel.com") ?: "auth.vercel.com",
                connectIp = prefs.getString("connect_ip", "188.114.98.0") ?: "188.114.98.0",
                connectPort = prefs.getString("connect_port", "443")?.toIntOrNull() ?: 443,
                listenPort = prefs.getString("listen_port", "40443")?.toIntOrNull() ?: 40443
            )
        }
    }

    fun toCommandLineArguments(): Array<String> {
        val args = mutableListOf<String>()

        args.add("ciadpi")

        // Strong desync settings (closest to wrong_seq + injection)
        args.add("--fake")
        args.add("-1")           // Very aggressive fake packet
        args.add("--disorder")
        args.add("1")
        args.add("--split")
        args.add("1+s")
        args.add("--tlsrec")
        args.add("1+s")
        args.add("--auto-ttl")
        args.add("--fake-sni")
        args.add(fakeSni)

        return args.toTypedArray()
    }
}