package com.focusflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusflow.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var sessionRepo: SessionRepository
    private lateinit var adapter: SessionHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionRepo = SessionRepository(this)
        setupUI()
        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener { finish() }
        
        adapter = SessionHistoryAdapter()
        
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }
    }

    private fun loadHistory() {
        val sessions = sessionRepo.getRecentSessions(50)
        adapter.submitList(sessions)
        
        binding.emptyState.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
        binding.historyRecyclerView.visibility = if (sessions.isEmpty()) View.GONE else View.VISIBLE
    }
}
