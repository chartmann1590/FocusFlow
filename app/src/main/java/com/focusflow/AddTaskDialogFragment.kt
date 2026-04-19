package com.focusflow

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.focusflow.databinding.DialogAddTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddTaskDialogFragment : DialogFragment() {
    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!
    
    var onTaskAdded: ((title: String, description: String, priority: Priority, category: Category, estimatedMinutes: Int) -> Unit)? = null
    private var selectedPriority = Priority.MEDIUM
    private var selectedCategory = Category.OTHER

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))

        binding.priorityLow.setOnClickListener { selectPriority(Priority.LOW) }
        binding.priorityMedium.setOnClickListener { selectPriority(Priority.MEDIUM) }
        binding.priorityHigh.setOnClickListener { selectPriority(Priority.HIGH) }
        selectPriority(Priority.MEDIUM)

        setupCategoryChips()

        binding.durationSlider.addOnChangeListener { _, value, _ ->
            binding.durationValue.text = "${value.toInt()} min"
        }
        binding.durationSlider.value = 25f

        binding.cancelButton.setOnClickListener { dismiss() }
        binding.addButton.setOnClickListener { addTask() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupCategoryChips() {
        binding.categoryWork.setOnClickListener { selectCategory(Category.WORK) }
        binding.categoryPersonal.setOnClickListener { selectCategory(Category.PERSONAL) }
        binding.categoryStudy.setOnClickListener { selectCategory(Category.STUDY) }
        binding.categoryHealth.setOnClickListener { selectCategory(Category.HEALTH) }
        binding.categoryCreative.setOnClickListener { selectCategory(Category.CREATIVE) }
        binding.categoryOther.setOnClickListener { selectCategory(Category.OTHER) }
        selectCategory(Category.OTHER)
    }

    private fun selectPriority(priority: Priority) {
        selectedPriority = priority
        binding.priorityLow.alpha = if (priority == Priority.LOW) 1f else 0.4f
        binding.priorityMedium.alpha = if (priority == Priority.MEDIUM) 1f else 0.4f
        binding.priorityHigh.alpha = if (priority == Priority.HIGH) 1f else 0.4f
    }

    private fun selectCategory(category: Category) {
        selectedCategory = category
        val alpha = { c: Category -> if (c == category) 1f else 0.4f }
        binding.categoryWork.alpha = alpha(Category.WORK)
        binding.categoryPersonal.alpha = alpha(Category.PERSONAL)
        binding.categoryStudy.alpha = alpha(Category.STUDY)
        binding.categoryHealth.alpha = alpha(Category.HEALTH)
        binding.categoryCreative.alpha = alpha(Category.CREATIVE)
        binding.categoryOther.alpha = alpha(Category.OTHER)
    }

    private fun addTask() {
        val title = binding.taskTitleInput.text.toString().trim()
        val description = binding.taskDescriptionInput.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.taskTitleInput.error = "Title required"
            return
        }

        val estimatedMinutes = binding.durationSlider.value.toInt()
        
        onTaskAdded?.invoke(title, description, selectedPriority, selectedCategory, estimatedMinutes)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
