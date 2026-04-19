package com.focusflow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focusflow.databinding.ItemArchiveBinding
import java.text.SimpleDateFormat
import java.util.*

class ArchiveAdapter(
    private val onRestore: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, ArchiveAdapter.ArchiveViewHolder>(ArchiveDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val binding = ItemArchiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArchiveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArchiveViewHolder(private val binding: ItemArchiveBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            
            val completedDate = task.completedAt?.let { dateFormat.format(Date(it)) } ?: "Unknown"
            binding.completedDate.text = "Completed: $completedDate"
            
            binding.pomodoroCount.text = "${task.completedPomodoros} pomodoros"
            
            binding.restoreButton.setOnClickListener { onRestore(task) }
            binding.deleteButton.setOnClickListener { onDelete(task) }
        }
    }

    class ArchiveDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}