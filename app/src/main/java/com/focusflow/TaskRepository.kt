package com.focusflow

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focusflow_tasks", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getTasks(): List<Task> {
        val json = prefs.getString("tasks", "[]") ?: "[]"
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getActiveTasks(): List<Task> = getTasks().filter { !it.isCompleted }

    fun getCompletedTasks(): List<Task> = getTasks().filter { it.isCompleted }

    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString("tasks", json).apply()
    }

    fun addTask(task: Task) {
        val tasks = getTasks().toMutableList()
        tasks.add(0, task)
        saveTasks(tasks)
    }

    fun updateTask(task: Task) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            saveTasks(tasks)
        }
    }

    fun deleteTask(taskId: String) {
        val tasks = getTasks().toMutableList()
        tasks.removeAll { it.id == taskId }
        saveTasks(tasks)
    }
}