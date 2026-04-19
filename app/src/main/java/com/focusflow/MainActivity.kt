package com.focusflow

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusflow.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }
    private lateinit var taskAdapter: TaskAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentFilter: TaskFilter = TaskFilter.ALL
    
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { showIdleReminder() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
        startIdleTimer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        idleHandler.removeCallbacks(idleRunnable)
    }

    private fun startIdleTimer() {
        resetIdleTimer()
    }

    private fun resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable)
        idleHandler.postDelayed(idleRunnable, 30 * 60 * 1000L)
    }

    private fun showIdleReminder() {
        val settings = SettingsRepository(this)
        if (settings.notificationsEnabled) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Time to focus!")
                .setMessage("You haven't completed a session in a while. Ready to start a new focus timer?")
                .setPositiveButton("Start Timer") { _, _ ->
                    startQuickTimer(settings.workDuration)
                }
                .setNegativeButton("Later", null)
                .show()
        }
    }

    data class TaskFilter(val category: Category?, val priority: Priority?, val showCompleted: Boolean) {
        companion object {
            val ALL = TaskFilter(null, null, true)
        }
    }

    private fun setupUI() {
        taskAdapter = TaskAdapter(
            onComplete = { task -> 
                viewModel.completePomodoro(task)
                showCelebration()
            },
            onDelete = { task -> viewModel.deleteTask(task.id) },
            onTimer = { task -> startTimer(task) },
            onPriorityChange = { task, priority -> viewModel.updatePriority(task, priority) },
            onEdit = { task -> showEditTaskDialog(task) }
        )
        
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }

        binding.addTaskButton.setOnClickListener { showAddTaskDialog() }
        binding.advancedAddButton.setOnClickListener { showAddTaskDialog() }

        binding.quickTimerLayout.preset15.setOnClickListener { startQuickTimer(15) }
        binding.quickTimerLayout.preset25.setOnClickListener { startQuickTimer(25) }
        binding.quickTimerLayout.preset45.setOnClickListener { startQuickTimer(45) }
        binding.quickTimerLayout.preset60.setOnClickListener { startQuickTimer(60) }
        
        binding.goalProgressLayout.breakTimerLayout.preset5.setOnClickListener { startBreakTimer(5) }
        binding.goalProgressLayout.breakTimerLayout.preset10.setOnClickListener { startBreakTimer(10) }
        binding.goalProgressLayout.breakTimerLayout.preset15.setOnClickListener { startBreakTimer(15) }

        binding.filterButton.setOnClickListener { showFilterDialog() }
        
        binding.statsButton.setOnClickListener { startActivity(Intent(this, StatsActivity::class.java)) }
        binding.settingsButton.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        binding.archiveButton.setOnClickListener { startActivity(Intent(this, ArchiveActivity::class.java)) }
        binding.historyButton.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        binding.weeklyButton.setOnClickListener { startActivity(Intent(this, WeeklyReportActivity::class.java)) }
        binding.soundsButton.setOnClickListener { startActivity(Intent(this, AmbientSoundsActivity::class.java)) }
    }

    private fun showFilterDialog() {
        val categories = listOf("All", "Work", "Personal", "Study", "Health", "Creative", "Other")
        val priorities = listOf("All", "High", "Medium", "Low")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Tasks")
            .setItems(arrayOf("Category: All", "Priority: All", "Show Completed: Yes")) { _, which ->
                when (which) {
                    0 -> showCategoryFilter()
                    1 -> showPriorityFilter()
                    2 -> toggleShowCompleted()
                }
            }
            .show()
    }
    
    private fun showCategoryFilter() {
        val categories = Category.entries.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter by Category")
            .setItems(arrayOf("All", "Work", "Personal", "Study", "Health", "Creative", "Other")) { _, which ->
                currentFilter = TaskFilter(
                    if (which == 0) null else categories[which - 1],
                    currentFilter.priority,
                    currentFilter.showCompleted
                )
                viewModel.setFilter(currentFilter)
            }
            .show()
    }
    
    private fun showPriorityFilter() {
        val priorities = Priority.entries.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter by Priority")
            .setItems(arrayOf("All", "High", "Medium", "Low")) { _, which ->
                currentFilter = TaskFilter(
                    currentFilter.category,
                    if (which == 0) null else priorities[which - 1],
                    currentFilter.showCompleted
                )
                viewModel.setFilter(currentFilter)
            }
            .show()
    }
    
    private fun toggleShowCompleted() {
        currentFilter = currentFilter.copy(showCompleted = !currentFilter.showCompleted)
        viewModel.setFilter(currentFilter)
    }

    private fun showCelebration() {
        binding.celebrationView.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.celebrationView.startAnimation(animation)
        binding.celebrationView.postDelayed({
            binding.celebrationView.visibility = View.GONE
        }, 1500)
    }

    private fun showAddTaskDialog() {
        val dialog = AddTaskDialogFragment()
        dialog.onTaskAdded = { title, description, priority, category, estimatedMinutes ->
            viewModel.addTask(title, description, priority, category, estimatedMinutes)
        }
        dialog.show(supportFragmentManager, "add_task")
    }

    private fun showEditTaskDialog(task: Task) {
        val dialog = AddTaskDialogFragment()
        dialog.onTaskAdded = { title, description, priority, category, estimatedMinutes ->
            val updated = task.copy(
                title = title,
                description = description,
                priority = priority,
                category = category,
                estimatedMinutes = estimatedMinutes
            )
            viewModel.updateTask(updated)
        }
        dialog.show(supportFragmentManager, "edit_task")
    }

    private fun startTimer(task: Task) {
        resetIdleTimer()
        TimerDialogFragment.newInstance(task.id, task.estimatedMinutes).show(supportFragmentManager, "timer")
    }

    private fun startQuickTimer(minutes: Int) {
        resetIdleTimer()
        TimerDialogFragment.newInstance("", minutes).show(supportFragmentManager, "quick_timer")
    }
    
    private fun startBreakTimer(minutes: Int) {
        resetIdleTimer()
        TimerDialogFragment.newInstance("", minutes, isBreak = true).show(supportFragmentManager, "break_timer")
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            val filtered = filterTasks(tasks)
            taskAdapter.submitList(filtered)
            updateEmptyState(filtered.isEmpty())
            updateFilterBadge()
        }

        viewModel.focusTime.observe(this) { minutes ->
            binding.focusTimeText.text = "${minutes}m"
            updateGoalProgress(minutes)
        }

        viewModel.sessionsCount.observe(this) { count ->
            binding.sessionsCountText.text = "$count sessions"
        }

        viewModel.currentStreak.observe(this) { streak ->
            binding.streakText.text = "🔥 $streak day streak"
            binding.streakText.visibility = if (streak > 0) View.VISIBLE else View.GONE
        }
    }
    
    private fun filterTasks(tasks: List<Task>): List<Task> {
        return tasks.filter { task ->
            val categoryMatch = currentFilter.category == null || task.category == currentFilter.category
            val priorityMatch = currentFilter.priority == null || task.priority == currentFilter.priority
            val completedMatch = currentFilter.showCompleted || !task.isCompleted
            categoryMatch && priorityMatch && completedMatch
        }
    }
    
    private fun updateFilterBadge() {
        val hasFilter = currentFilter.category != null || currentFilter.priority != null || !currentFilter.showCompleted
        binding.filterBadge.visibility = if (hasFilter) View.VISIBLE else View.GONE
    }

    private fun updateGoalProgress(currentMinutes: Int) {
        val settings = SettingsRepository(this)
        val goalMinutes = settings.dailyGoalMinutes
        val progress = ((currentMinutes.toFloat() / goalMinutes) * 100).toInt().coerceAtMost(100)
        binding.goalProgressLayout.goalProgress.progress = progress
        binding.goalProgressLayout.goalText.text = "${currentMinutes}/${goalMinutes}m"
        binding.goalProgressLayout.goalAchieved.visibility = if (currentMinutes >= goalMinutes) View.VISIBLE else View.GONE
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateGroup.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.tasksRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun playCompletionSound() {
        val settings = SettingsRepository(this)
        if (settings.soundEnabled) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (settings.vibrationEnabled) {
            vibrate()
        }
    }

    private fun vibrate() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        idleHandler.removeCallbacks(idleRunnable)
    }
}
