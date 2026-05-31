package io.github.dovecoteescapee.byedpi.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.databinding.ActivityMainBinding
import io.github.dovecoteescapee.byedpi.services.ByeDpiProxyService
import io.github.dovecoteescapee.byedpi.services.ByeDpiVpnService
import io.github.dovecoteescapee.byedpi.data.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        setupButtons()
        observeStatus()
    }
    
    private fun setupButtons() {
        binding.btnVpnConnect.setOnClickListener {
            startVpn()
        }
        
        binding.btnVpnDisconnect.setOnClickListener {
            stopVpn()
        }
        
        binding.btnProxyConnect.setOnClickListener {
            startProxy()
        }
        
        binding.btnProxyDisconnect.setOnClickListener {
            stopProxy()
        }
        
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun startVpn() {
        val intent = Intent(this, ByeDpiVpnService::class.java)
        intent.action = START_ACTION
        startService(intent)
    }
    
    private fun stopVpn() {
        val intent = Intent(this, ByeDpiVpnService::class.java)
        intent.action = STOP_ACTION
        startService(intent)
    }
    
    private fun startProxy() {
        val intent = Intent(this, ByeDpiProxyService::class.java)
        intent.action = START_ACTION
        startService(intent)
    }
    
    private fun stopProxy() {
        val intent = Intent(this, ByeDpiProxyService::class.java)
        intent.action = STOP_ACTION
        startService(intent)
    }
    
    private fun observeStatus() {
        // Add status observation logic here
    }
}