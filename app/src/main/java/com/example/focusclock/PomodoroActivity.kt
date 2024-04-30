package com.example.focusclock

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PomodoroActivity : AppCompatActivity() {
    lateinit var progressBar: ProgressBar
    lateinit var timer: CountDownTimer
    val totalTime = 1500000L // 25 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pomodoro)

        progressBar = findViewById(R.id.progressBar)

        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = millisUntilFinished.toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                // Timer finished, handle completion
            }
        }

        timer.start()

    }
}