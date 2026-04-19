package com.focusflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusflow.databinding.ActivityAmbientSoundsBinding

class AmbientSoundsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAmbientSoundsBinding
    private lateinit var soundRepo: AmbientSoundRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmbientSoundsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        soundRepo = AmbientSoundRepository(this)
        setupUI()
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener { finish() }
        
        val sounds = listOf(
            binding.soundNone to SoundType.NONE,
            binding.soundRain to SoundType.RAIN,
            binding.soundCafe to SoundType.CAFE,
            binding.soundForest to SoundType.FOREST,
            binding.soundOcean to SoundType.OCEAN,
            binding.soundWhiteNoise to SoundType.WHITE_NOISE
        )
        
        sounds.forEach { (card, sound) ->
            card.setOnClickListener {
                soundRepo.selectedSound = sound
                updateSelection(sounds.map { it.second })
                Toast.makeText(this, "${sound.displayName} selected", Toast.LENGTH_SHORT).show()
            }
        }
        
        updateSelection(sounds.map { it.second })
    }
    
    private fun updateSelection(sounds: List<SoundType>) {
        val selected = soundRepo.selectedSound
        binding.soundNone.isSelected = selected == SoundType.NONE
        binding.soundRain.isSelected = selected == SoundType.RAIN
        binding.soundCafe.isSelected = selected == SoundType.CAFE
        binding.soundForest.isSelected = selected == SoundType.FOREST
        binding.soundOcean.isSelected = selected == SoundType.OCEAN
        binding.soundWhiteNoise.isSelected = selected == SoundType.WHITE_NOISE
    }
}
