package com.focusflow

data class DailyStats(
    val date: String,
    val focusMinutes: Int,
    val sessions: Int,
    val tasksCompleted: Int
)
