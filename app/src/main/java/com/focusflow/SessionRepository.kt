package com.focusflow

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focusflow_sessions", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSessions(): List<FocusSession> {
        val json = prefs.getString("sessions", "[]") ?: "[]"
        val type = object : TypeToken<List<FocusSession>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addSession(session: FocusSession) {
        val sessions = getSessions().toMutableList()
        sessions.add(0, session)
        if (sessions.size > 100) sessions.removeAt(sessions.size - 1)
        saveSessions(sessions)
    }

    private fun saveSessions(sessions: List<FocusSession>) {
        val json = gson.toJson(sessions)
        prefs.edit().putString("sessions", json).apply()
    }

    fun getRecentSessions(limit: Int = 10): List<FocusSession> {
        return getSessions().take(limit)
    }
}
