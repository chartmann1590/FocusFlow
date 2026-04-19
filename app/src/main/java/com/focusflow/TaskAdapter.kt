package com.focusflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focusflow.databinding.ItemTaskBinding

class TaskAdapter(
    private val onComplete: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onTimer: (Task) -> Unit,
    private val onPriorityChange: (Task, Priority) -> Unit,
    private val onEdit: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.categoryIcon.text = task.category.icon
            binding.pomodoroCount.text = "${task.completedPomodoros}/${task.estimatedMinutes}min"
            
            if (task.description.isNotEmpty()) {
                binding.taskDescription.visibility = View.VISIBLE
                binding.taskDescription.text = task.description
            } else {
                binding.taskDescription.visibility = View.GONE
            }
            
            val priorityColor = when (task.priority) {
                Priority.HIGH -> R.color.priority_high
                Priority.MEDIUM -> R.color.priority_medium
                Priority.LOW -> R.color.priority_low
            }
            binding.priorityIndicator.setBackgroundColor(binding.root.context.getColor(priorityColor))
            binding.priorityIndicator.visibility = View.VISIBLE

            binding.moreButton.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menu.add(0, 0, 0, "Edit").setOnMenuItemClickListener {
                    onEdit(task)
                    true
                }
                popup.menu.add(0, 1, 1, "Set High Priority").setOnMenuItemClickListener {
                    onPriorityChange(task, Priority.HIGH)
                    true
                }
                popup.menu.add(0, 2, 2, "Set Medium Priority").setOnMenuItemClickListener {
                    onPriorityChange(task, Priority.MEDIUM)
                    true
                }
                popup.menu.add(0, 3, 3, "Set Low Priority").setOnMenuItemClickListener {
                    onPriorityChange(task, Priority.LOW)
                    true
                }
                popup.menu.add(0, 4, 4, "Mark Complete").setOnMenuItemClickListener {
                    onComplete(task)
                    true
                }
                popup.menu.add(0, 5, 5, "Delete").setOnMenuItemClickListener {
                    onDelete(task)
                    true
                }
                popup.show()
            }
            
            binding.timerButton.setOnClickListener { onTimer(task) }
            binding.completeButton.setOnClickListener { onComplete(task) }
            binding.deleteButton.setOnClickListener { onDelete(task) }

            binding.root.alpha = if (task.isCompleted) 0.5f else 1.0f
            binding.root.isEnabled = !task.isCompleted
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}