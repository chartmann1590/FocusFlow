package com.focusflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.focusflow.databinding.ActivitySettingsBinding
import com.google.android.material.card.MaterialCardView

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settings: SettingsRepository
    private lateinit var updateChecker: UpdateChecker
    
    private var latestRelease: ReleaseInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        settings = SettingsRepository(this)
        updateChecker = UpdateChecker(this)
        
        loadSettings()
        setupListeners()
        checkForUpdates()
    }
    
    private fun checkForUpdates() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val currentVersion = packageInfo.versionName ?: "1.0"
        binding.versionText.text = "v$currentVersion"
        
        updateChecker.checkForUpdate(currentVersion, object : UpdateChecker.UpdateCallback {
            override fun onUpdateAvailable(latestRelease: ReleaseInfo, currentVersion: String) {
                this@SettingsActivity.latestRelease = latestRelease
                runOnUiThread {
                    showUpdateBanner(latestRelease, currentVersion)
                }
            }
            
            override fun onNoUpdateAvailable(currentVersion: String) {
                runOnUiThread {
                    binding.updateBanner.visibility = View.GONE
                }
            }
            
            override fun onError(error: String) {
                runOnUiThread {
                    binding.updateBanner.visibility = View.GONE
                }
            }
        })
    }
    
    private fun showUpdateBanner(release: ReleaseInfo, currentVersion: String) {
        binding.updateBanner.visibility = View.VISIBLE
        binding.updateBannerText.text = "Update available: ${release.version}"
        binding.updateBanner.setOnClickListener {
            showUpdateDialog(release, currentVersion)
        }
    }
    
    private fun showUpdateDialog(release: ReleaseInfo, currentVersion: String) {
        AlertDialog.Builder(this)
            .setTitle("Update Available")
            .setMessage("A new version (${release.version}) is available!\n\nCurrent: $currentVersion\nLatest: ${release.version}\n\nWould you like to download it?")
            .setPositiveButton("Download") { _, _ ->
                updateChecker.downloadLatestRelease(release)
            }
            .setNeutralButton("View on GitHub") { _, _ ->
                updateChecker.openReleases()
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun loadSettings() {
        binding.workDurationValue.text = "${settings.workDuration} min"
        binding.workDurationSeek.progress = settings.workDuration - 5
        
        binding.shortBreakValue.text = "${settings.shortBreakDuration} min"
        binding.shortBreakSeek.progress = settings.shortBreakDuration - 1
        
        binding.longBreakValue.text = "${settings.longBreakDuration} min"
        binding.longBreakSeek.progress = settings.longBreakDuration - 5
        
        binding.sessionsValue.text = "${settings.sessionsUntilLongBreak} sessions"
        binding.sessionsSeek.progress = settings.sessionsUntilLongBreak - 1
        
        binding.soundSwitch.isChecked = settings.soundEnabled
        binding.vibrationSwitch.isChecked = settings.vibrationEnabled
        binding.autoBreakSwitch.isChecked = settings.autoStartBreaks
        binding.autoWorkSwitch.isChecked = settings.autoStartWork
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }
        
        binding.workDurationSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 5
                binding.workDurationValue.text = "$value min"
                settings.workDuration = value
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        binding.shortBreakSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                binding.shortBreakValue.text = "$value min"
                settings.shortBreakDuration = value
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        binding.longBreakSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 5
                binding.longBreakValue.text = "$value min"
                settings.longBreakDuration = value
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        binding.sessionsSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                binding.sessionsValue.text = "$value sessions"
                settings.sessionsUntilLongBreak = value
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.soundEnabled = isChecked
        }
        
        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.vibrationEnabled = isChecked
        }
        
        binding.autoBreakSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.autoStartBreaks = isChecked
        }
        
        binding.autoWorkSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.autoStartWork = isChecked
        }
        
        binding.coffeeSection.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, 
                android.net.Uri.parse("https://www.buymeacoffee.com/charleshartmann"))
            startActivity(intent)
        }
        
        binding.websiteSection.setOnClickListener {
            updateChecker.openWebsite()
        }
        
        binding.githubSection.setOnClickListener {
            updateChecker.openGitHub()
        }
    }
}
