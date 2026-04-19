package com.focusflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.focusflow.databinding.ActivityWeeklyReportBinding
import java.text.SimpleDateFormat
import java.util.*

class WeeklyReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeeklyReportBinding
    private lateinit var statsRepo: StatsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        statsRepo = StatsRepository(this)
        loadReport()
    }

    private fun loadReport() {
        val weekStats = statsRepo.getWeekStats()
        val (totalMinutes, totalSessions, totalTasks) = statsRepo.getTotalStats()
        
        val weekMinutes = weekStats.sumOf { it.focusMinutes }
        val weekSessions = weekStats.sumOf { it.sessions }
        val weekTasks = weekStats.sumOf { it.tasksCompleted }
        
        binding.weekFocus.text = formatDuration(weekMinutes)
        binding.weekSessions.text = "$weekSessions"
        binding.weekTasks.text = "$weekTasks"
        
        binding.totalFocus.text = formatDuration(totalMinutes)
        binding.totalSessions.text = "$totalSessions"
        binding.totalTasks.text = "$totalTasks"
        
        val avgPerDay = if (weekStats.isNotEmpty()) weekMinutes / 7 else 0
        binding.avgDaily.text = formatDuration(avgPerDay)
        
        val bestDay = weekStats.maxByOrNull { it.focusMinutes }
        binding.bestDay.text = if (bestDay != null && bestDay.focusMinutes > 0) {
            "${bestDay.date.takeLast(5)} - ${bestDay.focusMinutes}m"
        } else {
            "No data"
        }
        
        binding.currentStreak.text = "${statsRepo.getCurrentStreak()} days"
        binding.longestStreak.text = "${statsRepo.getLongestStreak()} days"
        
        val chartViews = listOf(
            binding.day1Bar, binding.day2Bar, binding.day3Bar,
            binding.day4Bar, binding.day5Bar, binding.day6Bar, binding.day7Bar
        )
        val dayLabels = listOf(
            binding.day1Label, binding.day2Label, binding.day3Label,
            binding.day4Label, binding.day5Label, binding.day6Label, binding.day7Label
        )
        
        val maxFocus = weekStats.maxOfOrNull { it.focusMinutes } ?: 1
        weekStats.forEachIndexed { index, stats ->
            if (index < 7) {
                val height = if (maxFocus > 0) (stats.focusMinutes.toFloat() / maxFocus * 100).toInt() else 0
                chartViews[index].layoutParams.height = (height * 2.5).toInt().coerceAtLeast(4)
                chartViews[index].visibility = View.VISIBLE
                dayLabels[index].text = stats.date.takeLast(5).replace("-", "/")
            }
        }
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
