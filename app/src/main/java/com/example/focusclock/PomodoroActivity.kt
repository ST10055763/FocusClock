package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PomodoroActivity : AppCompatActivity() {
    lateinit var progressBar25Min: ProgressBar
    lateinit var tvTimer25Min: TextView
    lateinit var progressBar5Min: ProgressBar
    lateinit var tvTimer5Min: TextView
    lateinit var timer25Min: CountDownTimer
    lateinit var timer5Min: CountDownTimer
    lateinit var redirect: ImageButton
    val totalTime = 1500000L // 25 minutes in milliseconds
    val totalTime5Min = 300000L // 5 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        progressBar25Min = findViewById(R.id.progressBar)
        tvTimer25Min = findViewById(R.id.tvTimer)
        progressBar5Min = findViewById(R.id.progressBar5Min)
        tvTimer5Min = findViewById(R.id.tvTimer5Min)

        val startSessionbutton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)
        startSessionbutton.setOnClickListener {
            startSession()
        }
        stopButton.setOnClickListener {
            stopSession()
        }

        redirect = findViewById(R.id.btnTasksRedirect)
        redirect.setOnClickListener{
            var AddTasksIntent = Intent(this, AddATaskActivity::class.java)
            startActivity(AddTasksIntent)
        }

    }

    private fun startSession() {
        startTimer25Min()
    }

    private fun stopSession() {
        timer25Min.cancel()
        timer5Min.cancel()
    }

    private fun startTimer25Min() {
        timer25Min = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                updateTimerText(tvTimer25Min, secondsRemaining)
                updateProgressBar(progressBar25Min, totalTime, secondsRemaining)
            }

            override fun onFinish() {
                startTimer5Min()
            }
        }

        timer25Min.start()
    }
    private fun startTimer5Min() {
        timer5Min = object : CountDownTimer(totalTime5Min, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                updateTimerText(tvTimer5Min, secondsRemaining)
                updateProgressBar(progressBar5Min, totalTime5Min, secondsRemaining)
            }

            override fun onFinish() {
                startTimer25Min()
            }
        }

        timer5Min.start()
    }

    private fun updateTimerText(textView: TextView, secondsRemaining: Long) {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        textView.text = timeLeftFormatted
    }

    private fun updateProgressBar(progressBar: ProgressBar, totalTime: Long, secondsRemaining: Long) {
        val progress = (totalTime - secondsRemaining * 1000).toInt()
        progressBar.progress = progress
    }
}