package com.example.focusclock

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PomodoroActivity : AppCompatActivity() {
    lateinit var progressBar: ProgressBar
    lateinit var tvTimer: TextView
    lateinit var timer: CountDownTimer
    val totalTime = 1500000L // 25 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        progressBar = findViewById(R.id.progressBar)
        tvTimer = findViewById(R.id.tvTimer)

        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                updateTimerText(secondsRemaining)
                updateProgressBar(secondsRemaining)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
            }
        }

        timer.start()
    }

    private fun updateTimerText(secondsRemaining: Long) {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        tvTimer.text = timeLeftFormatted
    }

    private fun updateProgressBar(secondsRemaining: Long) {
        val progress = (totalTime - secondsRemaining * 1000).toInt()
        progressBar.progress = progress
    }
}