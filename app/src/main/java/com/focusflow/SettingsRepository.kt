package com.focusflow

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focusflow_settings", Context.MODE_PRIVATE)

    var workDuration: Int
        get() = prefs.getInt("work_duration", 25)
        set(value) = prefs.edit().putInt("work_duration", value).apply()

    var shortBreakDuration: Int
        get() = prefs.getInt("short_break", 5)
        set(value) = prefs.edit().putInt("short_break", value).apply()

    var longBreakDuration: Int
        get() = prefs.getInt("long_break", 15)
        set(value) = prefs.edit().putInt("long_break", value).apply()

    var sessionsUntilLongBreak: Int
        get() = prefs.getInt("sessions_until_long", 4)
        set(value) = prefs.edit().putInt("sessions_until_long", value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var autoStartBreaks: Boolean
        get() = prefs.getBoolean("auto_start_breaks", false)
        set(value) = prefs.edit().putBoolean("auto_start_breaks", value).apply()

    var autoStartWork: Boolean
        get() = prefs.getBoolean("auto_start_work", false)
        set(value) = prefs.edit().putBoolean("auto_start_work", value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean("dark_theme", true)
        set(value) = prefs.edit().putBoolean("dark_theme", value).apply()

    var focusModeEnabled: Boolean
        get() = prefs.getBoolean("focus_mode", false)
        set(value) = prefs.edit().putBoolean("focus_mode", value).apply()

    var dailyGoalMinutes: Int
        get() = prefs.getInt("daily_goal", 120)
        set(value) = prefs.edit().putInt("daily_goal", value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()
}
