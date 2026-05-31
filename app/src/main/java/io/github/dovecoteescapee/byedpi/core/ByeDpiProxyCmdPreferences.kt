package io.github.dovecoteescapee.byedpi.core

import android.content.SharedPreferences

class ByeDpiProxyCmdPreferences(
    private val commandLine: String
) : ByeDpiProxyPreferences {

    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences): ByeDpiProxyCmdPreferences {
            val cmdLine = prefs.getString("command_line", "") ?: ""
            return ByeDpiProxyCmdPreferences(cmdLine)
        }
    }

    override fun toCommandLineArguments(): Array<String> {
        if (commandLine.isBlank()) {
            return arrayOf("ciadpi")
        }
        return commandLine.split("\\s+".toRegex()).toTypedArray()
    }
}