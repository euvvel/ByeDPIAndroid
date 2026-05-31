package io.github.dovecoteescapee.byedpi.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxyUIPreferences
import io.github.dovecoteescapee.byedpi.core.DesyncMethod
import io.github.dovecoteescapee.byedpi.core.HostsMode

class ByeDpiUISettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.byedpi_ui_settings, rootKey)
        
        setupHostsModePreference()
        setupDesyncMethodPreference()
        setupListeners()
    }

    private fun setupHostsModePreference() {
        val hostsModePref = findPreference<ListPreference>("hosts_mode")
        hostsModePref?.apply {
            entries = arrayOf(
                getString(R.string.hosts_mode_none),
                getString(R.string.hosts_mode_blacklist),
                getString(R.string.hosts_mode_whitelist)
            )
            entryValues = arrayOf(
                HostsMode.None.name,
                HostsMode.Blacklist.name,
                HostsMode.Whitelist.name
            )
        }
    }

    private fun setupDesyncMethodPreference() {
        val desyncMethodPref = findPreference<ListPreference>("desync_method")
        desyncMethodPref?.apply {
            entries = arrayOf(
                getString(R.string.desync_method_fake),
                getString(R.string.desync_method_oob),
                getString(R.string.desync_method_disoob)
            )
            entryValues = arrayOf(
                DesyncMethod.Fake.name,
                DesyncMethod.OOB.name,
                DesyncMethod.DISOOB.name
            )
        }
    }

    private fun setupListeners() {
        val sharedPreferences = preferenceManager.sharedPreferences
        
        // Hosts mode listener
        findPreference<ListPreference>("hosts_mode")?.setOnPreferenceChangeListener { _, newValue ->
            val hostsMode = try {
                HostsMode.valueOf(newValue as String)
            } catch (e: IllegalArgumentException) {
                HostsMode.None
            }
            
            when (hostsMode) {
                HostsMode.Blacklist -> {
                    // Enable blacklist specific settings
                    findPreference<androidx.preference.EditTextPreference>("blacklist_hosts")?.isEnabled = true
                }
                HostsMode.Whitelist -> {
                    // Enable whitelist specific settings
                    findPreference<androidx.preference.EditTextPreference>("whitelist_hosts")?.isEnabled = true
                }
                HostsMode.None -> {
                    // Disable hosts settings
                    findPreference<androidx.preference.EditTextPreference>("blacklist_hosts")?.isEnabled = false
                    findPreference<androidx.preference.EditTextPreference>("whitelist_hosts")?.isEnabled = false
                }
            }
            true
        }
        
        // Desync method listener
        findPreference<ListPreference>("desync_method")?.setOnPreferenceChangeListener { _, newValue ->
            val desyncMethod = try {
                DesyncMethod.valueOf(newValue as String)
            } catch (e: IllegalArgumentException) {
                DesyncMethod.Fake
            }
            
            when (desyncMethod) {
                DesyncMethod.Fake -> {
                    // Show fake-specific settings
                    findPreference<androidx.preference.EditTextPreference>("fake_sni")?.isEnabled = true
                    findPreference<androidx.preference.EditTextPreference>("fake_ttl")?.isEnabled = true
                }
                DesyncMethod.OOB -> {
                    // Show OOB-specific settings
                    findPreference<androidx.preference.EditTextPreference>("oob_char")?.isEnabled = true
                }
                DesyncMethod.DISOOB -> {
                    // Show DISOOB-specific settings
                    findPreference<androidx.preference.EditTextPreference>("fake_sni")?.isEnabled = true
                }
            }
            true
        }
    }

    companion object {
        fun getPreferences(prefs: SharedPreferences): ByeDpiProxyUIPreferences {
            return ByeDpiProxyUIPreferences.fromSharedPreferences(prefs)
        }
    }
}