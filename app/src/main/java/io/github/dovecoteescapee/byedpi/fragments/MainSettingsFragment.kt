package io.github.dovecoteescapee.byedpi.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.dovecoteescapee.byedpi.BuildConfig
import io.github.dovecoteescapee.byedpi.R

class MainSettingsFragment : PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        
        setupVersionPreference()
        setupGithubPreference()
    }
    
    private fun setupVersionPreference() {
        findPreference<Preference>("app_version")?.apply {
            summary = BuildConfig.VERSION_NAME
        }
    }
    
    private fun setupGithubPreference() {
        findPreference<Preference>("github_link")?.apply {
            setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dovecoteescapee/ByeDPIAndroid"))
                startActivity(intent)
                true
            }
        }
    }
}