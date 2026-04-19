package com.focusflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusflow.databinding.ActivityArchiveBinding

class ArchiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArchiveBinding
    private lateinit var repository: TaskRepository
    private lateinit var adapter: ArchiveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        repository = TaskRepository(this)
        setupUI()
        loadArchive()
    }

    override fun onResume() {
        super.onResume()
        loadArchive()
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener { finish() }
        
        adapter = ArchiveAdapter(
            onRestore = { task -> restoreTask(task) },
            onDelete = { task -> deleteTask(task.id) }
        )
        
        binding.archiveRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            adapter = this@ArchiveActivity.adapter
        }
    }

    private fun loadArchive() {
        val completedTasks = repository.getCompletedTasks()
            .sortedByDescending { it.completedAt }
        
        adapter.submitList(completedTasks)
        
        binding.emptyState.visibility = if (completedTasks.isEmpty()) View.VISIBLE else View.GONE
        binding.archiveRecyclerView.visibility = if (completedTasks.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun restoreTask(task: Task) {
        val restored = task.copy(isCompleted = false, completedAt = null)
        repository.updateTask(restored)
        loadArchive()
    }

    private fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        loadArchive()
    }
}