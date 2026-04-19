package com.focusflow

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.focusflow.databinding.DialogTimerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TimerDialogFragment : DialogFragment() {
    private var _binding: DialogTimerBinding? = null
    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 25 * 60 * 1000L
    private var totalTime: Long = 25 * 60 * 1000L
    private var isRunning = false
    private var isWorkSession = true
    private var isBreakMode = false
    private var completedSessions = 0

    companion object {
        private const val ARG_TASK_ID = "task_id"
        private const val ARG_DURATION = "duration"
        private const val ARG_IS_BREAK = "is_break"
        
        fun newInstance(taskId: String, duration: Int = 25, isBreak: Boolean = false): TimerDialogFragment {
            return TimerDialogFragment().apply {
                arguments = Bundle().apply { 
                    putString(ARG_TASK_ID, taskId)
                    putInt(ARG_DURATION, duration)
                    putBoolean(ARG_IS_BREAK, isBreak)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTimerBinding.inflate(LayoutInflater.from(context))
        
        val settings = SettingsRepository(requireContext())
        val duration = arguments?.getInt(ARG_DURATION) ?: settings.workDuration
        isBreakMode = arguments?.getBoolean(ARG_IS_BREAK) ?: false
        
        if (isBreakMode) {
            totalTime = duration * 60 * 1000L
            isWorkSession = false
        } else {
            totalTime = duration * 60 * 1000L
            isWorkSession = true
        }
        timeLeft = totalTime
        
        savedInstanceState?.getLong("timeLeft")?.let { timeLeft = it }
        savedInstanceState?.getBoolean("isRunning")?.let { 
            if (it) startTimer(timeLeft)
        }

        updateDisplay()

        binding.startPauseButton.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer(timeLeft)
        }

        binding.resetButton.setOnClickListener { resetTimer() }
        binding.skipButton.setOnClickListener { skipSession() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create().apply {
                window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
    }

    private fun startTimer(millis: Long) {
        isRunning = true
        binding.startPauseButton.text = "Pause"
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisLeft: Long) {
                timeLeft = millisLeft
                updateDisplay()
            }
            override fun onFinish() {
                onTimerComplete()
            }
        }.start()
    }

    private fun pauseTimer() {
        isRunning = false
        binding.startPauseButton.text = "Resume"
        timer?.cancel()
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        timeLeft = totalTime
        binding.startPauseButton.text = "Start"
        updateDisplay()
    }
    
    private fun skipSession() {
        timer?.cancel()
        isWorkSession = !isWorkSession
        val settings = SettingsRepository(requireContext())
        totalTime = if (isWorkSession) {
            settings.workDuration * 60 * 1000L
        } else {
            settings.shortBreakDuration * 60 * 1000L
        }
        timeLeft = totalTime
        isRunning = false
        binding.startPauseButton.text = "Start"
        updateDisplay()
    }

    private fun updateDisplay() {
        val minutes = (timeLeft / 1000) / 60
        val seconds = (timeLeft / 1000) % 60
        binding.timerDisplay.text = String.format("%02d:%02d", minutes, seconds)
        binding.timerProgress.progress = ((timeLeft.toFloat() / totalTime) * 100).toInt()
        
        val sessionText = if (isBreakMode) {
            "Break Time"
        } else {
            "Focus ($completedSessions sessions)"
        }
        binding.sessionType.text = sessionText
    }

    private fun onTimerComplete() {
        isRunning = false
        binding.startPauseButton.text = "Start"
        
        if (isWorkSession && !isBreakMode) {
            completedSessions++
        }
        
        val mainActivity = activity as? MainActivity
        mainActivity?.playCompletionSound()
        
        binding.timerDisplay.text = "Done!"
        
        val settings = SettingsRepository(requireContext())
        if (settings.autoStartBreaks && isWorkSession && !isBreakMode) {
            isWorkSession = false
            totalTime = settings.shortBreakDuration * 60 * 1000L
            timeLeft = totalTime
            updateDisplay()
            startTimer(timeLeft)
        } else if (settings.autoStartWork && !isWorkSession) {
            isWorkSession = true
            totalTime = settings.workDuration * 60 * 1000L
            timeLeft = totalTime
            updateDisplay()
            startTimer(timeLeft)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeft", timeLeft)
        outState.putBoolean("isRunning", isRunning)
        outState.putBoolean("isWorkSession", isWorkSession)
        outState.putInt("completedSessions", completedSessions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}