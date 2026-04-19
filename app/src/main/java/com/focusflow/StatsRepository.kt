package com.focusflow

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class StatsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focusflow_all_stats", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getToday(): DailyStats {
        val today = dateFormat.format(Date())
        return DailyStats(
            date = today,
            focusMinutes = prefs.getInt("focus_$today", 0),
            sessions = prefs.getInt("sessions_$today", 0),
            tasksCompleted = prefs.getInt("tasks_$today", 0)
        )
    }

    fun addFocusMinutes(minutes: Int) {
        val today = dateFormat.format(Date())
        val current = prefs.getInt("focus_$today", 0)
        prefs.edit().putInt("focus_$today", current + minutes).apply()
    }

    fun addSession() {
        val today = dateFormat.format(Date())
        val current = prefs.getInt("sessions_$today", 0)
        prefs.edit().putInt("sessions_$today", current + 1).apply()
    }

    fun addCompletedTask() {
        val today = dateFormat.format(Date())
        val current = prefs.getInt("tasks_$today", 0)
        prefs.edit().putInt("tasks_$today", current + 1).apply()
        updateStreak()
    }

    fun getWeekStats(): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val stats = mutableListOf<DailyStats>()
        
        repeat(7) {
            val date = dateFormat.format(calendar.time)
            stats.add(DailyStats(
                date = date,
                focusMinutes = prefs.getInt("focus_$date", 0),
                sessions = prefs.getInt("sessions_$date", 0),
                tasksCompleted = prefs.getInt("tasks_$date", 0)
            ))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return stats.reversed()
    }

    fun getTotalStats(): Triple<Int, Int, Int> {
        var totalMinutes = 0
        var totalSessions = 0
        var totalTasks = 0
        
        prefs.all.forEach { (key, value) ->
            when {
                key.startsWith("focus_") -> totalMinutes += value as Int
                key.startsWith("sessions_") -> totalSessions += value as Int
                key.startsWith("tasks_") -> totalTasks += value as Int
            }
        }
        return Triple(totalMinutes, totalSessions, totalTasks)
    }

    fun getCurrentStreak(): Int {
        val calendar = Calendar.getInstance()
        var streak = 0
        
        repeat(365) {
            val date = dateFormat.format(calendar.time)
            val tasks = prefs.getInt("tasks_$date", 0)
            if (tasks > 0) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                return streak
            }
        }
        return streak
    }

    fun getLongestStreak(): Int {
        return prefs.getInt("longest_streak", 0)
    }

    private fun updateStreak() {
        val currentStreak = getCurrentStreak()
        val longestStreak = getLongestStreak()
        if (currentStreak > longestStreak) {
            prefs.edit().putInt("longest_streak", currentStreak).apply()
        }
    }
}
