package com.focusflow

import android.graphics.Color

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Category(val color: Int, val icon: String) {
    WORK(Color.parseColor("#E94560"), "💼"),
    PERSONAL(Color.parseColor("#4ECDC4"), "🏠"),
    STUDY(Color.parseColor("#FFE66D"), "📚"),
    HEALTH(Color.parseColor("#95E1D3"), "💪"),
    CREATIVE(Color.parseColor("#DDA0DD"), "🎨"),
    OTHER(Color.parseColor("#9E9E9E"), "📌")
}

data class Task(
    val id: String = System.currentTimeMillis().toString(),
    var title: String,
    var description: String = "",
    var priority: Priority = Priority.MEDIUM,
    var category: Category = Category.OTHER,
    var estimatedMinutes: Int = 25,
    var pomodoros: Int = 0,
    var completedPomodoros: Int = 0,
    var isCompleted: Boolean = false,
    var completedAt: Long? = null,
    var createdAt: Long = System.currentTimeMillis()
)

data class FocusSession(
    val id: String = System.currentTimeMillis().toString(),
    val taskId: String?,
    val taskTitle: String,
    val duration: Int,
    val completedAt: Long = System.currentTimeMillis()
)
