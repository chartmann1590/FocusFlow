package com.focusflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.focusflow.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var statsRepo: StatsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        statsRepo = StatsRepository(this)
        loadStats()
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun loadStats() {
        val today = statsRepo.getToday()
        val weekStats = statsRepo.getWeekStats()
        val (totalMinutes, totalSessions, totalTasks) = statsRepo.getTotalStats()

        binding.todayFocus.text = "${today.focusMinutes}m"
        binding.todaySessions.text = "${today.sessions}"
        binding.todayTasks.text = "${today.tasksCompleted}"

        binding.totalFocus.text = formatDuration(totalMinutes)
        binding.totalSessions.text = "$totalSessions"
        binding.totalTasks.text = "$totalTasks"

        val maxFocus = weekStats.maxOfOrNull { stat: DailyStats -> stat.focusMinutes } ?: 1
        val chartViews = listOf(
            binding.day1Bar, binding.day2Bar, binding.day3Bar,
            binding.day4Bar, binding.day5Bar, binding.day6Bar, binding.day7Bar
        )
        val dayLabels = listOf(
            binding.day1Label, binding.day2Label, binding.day3Label,
            binding.day4Label, binding.day5Label, binding.day6Label, binding.day7Label
        )

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
