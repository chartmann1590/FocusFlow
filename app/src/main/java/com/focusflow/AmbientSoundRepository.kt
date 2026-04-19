package com.focusflow

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class SoundType(val displayName: String) {
    NONE("None"),
    RAIN("Rain"),
    CAFE("Coffee Shop"),
    FOREST("Forest"),
    OCEAN("Ocean Waves"),
    WHITE_NOISE("White Noise")
}

class AmbientSoundRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focusflow_sounds", Context.MODE_PRIVATE)

    var selectedSound: SoundType
        get() = SoundType.entries.getOrNull(prefs.getInt("selected_sound", 0)) ?: SoundType.NONE
        set(value) = prefs.edit().putInt("selected_sound", value.ordinal).apply()

    var volume: Float
        get() = prefs.getFloat("volume", 0.5f)
        set(value) = prefs.edit().putFloat("volume", value).apply()
}
