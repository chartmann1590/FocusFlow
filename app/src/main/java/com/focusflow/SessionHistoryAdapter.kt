package com.focusflow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focusflow.databinding.ItemSessionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class SessionHistoryAdapter : ListAdapter<FocusSession, SessionHistoryAdapter.SessionViewHolder>(SessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SessionViewHolder(private val binding: ItemSessionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        
        fun bind(session: FocusSession) {
            binding.taskTitle.text = session.taskTitle.ifEmpty { "Quick Timer" }
            binding.sessionDuration.text = "${session.duration} min"
            
            val date = Date(session.completedAt)
            binding.sessionTime.text = "${dateFormat.format(date)} at ${timeFormat.format(date)}"
        }
    }

    class SessionDiffCallback : DiffUtil.ItemCallback<FocusSession>() {
        override fun areItemsTheSame(oldItem: FocusSession, newItem: FocusSession) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FocusSession, newItem: FocusSession) = oldItem == newItem
    }
}
