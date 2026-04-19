package com.focusflow

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(application)
    private val statsRepo = StatsRepository(application)
    private val sessionRepo = SessionRepository(application)
    
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    
    private val _focusTime = MutableLiveData(0)
    val focusTime: LiveData<Int> = _focusTime
    
    private val _sessionsCount = MutableLiveData(0)
    val sessionsCount: LiveData<Int> = _sessionsCount
    
    private val _currentStreak = MutableLiveData(0)
    val currentStreak: LiveData<Int> = _currentStreak
    
    private val _recentSessions = MutableLiveData<List<FocusSession>>()
    val recentSessions: LiveData<List<FocusSession>> = _recentSessions
    
    private var currentFilter = MainActivity.TaskFilter.ALL

    init {
        loadTasks()
        loadStats()
        loadRecentSessions()
    }

    fun refresh() {
        loadTasks()
        loadStats()
        loadRecentSessions()
    }
    
    fun setFilter(filter: MainActivity.TaskFilter) {
        currentFilter = filter
        loadTasks()
    }

    private fun loadTasks() {
        val allTasks = repository.getTasks().sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenByDescending { it.priority.ordinal }
                .thenByDescending { it.createdAt }
        )
        _tasks.value = allTasks
    }

    private fun loadStats() {
        val today = statsRepo.getToday()
        _focusTime.value = today.focusMinutes
        _sessionsCount.value = today.sessions
        _currentStreak.value = statsRepo.getCurrentStreak()
    }
    
    private fun loadRecentSessions() {
        _recentSessions.value = sessionRepo.getRecentSessions(10)
    }

    fun addTask(title: String, description: String = "", priority: Priority = Priority.MEDIUM, category: Category = Category.OTHER, estimatedMinutes: Int = 25) {
        val task = Task(
            title = title,
            description = description,
            priority = priority,
            category = category,
            estimatedMinutes = estimatedMinutes
        )
        repository.addTask(task)
        loadTasks()
    }

    fun updateTask(task: Task) {
        repository.updateTask(task)
        loadTasks()
    }

    fun completePomodoro(task: Task) {
        val updated = task.copy(completedPomodoros = task.completedPomodoros + 1)
        repository.updateTask(updated)
        
        sessionRepo.addSession(FocusSession(
            taskId = task.id,
            taskTitle = task.title,
            duration = task.estimatedMinutes
        ))
        
        loadTasks()
        loadStats()
        loadRecentSessions()
    }

    fun completeTask(task: Task) {
        val completed = task.copy(
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        repository.updateTask(completed)
        statsRepo.addCompletedTask()
        loadTasks()
        loadStats()
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        loadTasks()
    }

    fun updatePriority(task: Task, priority: Priority) {
        val updated = task.copy(priority = priority)
        repository.updateTask(updated)
        loadTasks()
    }
}

class MainViewModelFactory(private val app: android.app.Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
