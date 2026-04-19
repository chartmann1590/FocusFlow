package com.focusflow

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.focusflow.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        settings = SettingsRepository(this)
        loadSettings()
        setupListeners()
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
    }
}