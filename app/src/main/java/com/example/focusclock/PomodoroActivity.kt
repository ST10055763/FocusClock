package com.example.focusclock

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PomodoroActivity : AppCompatActivity() {
    lateinit var progressBar25Min: ProgressBar
    lateinit var tvTimer25Min: TextView
    lateinit var progressBar5Min: ProgressBar
    lateinit var selectedAProject: Spinner
    lateinit var selectedATask: Spinner
    lateinit var tvTimer5Min: TextView
    lateinit var timer25Min: CountDownTimer
    lateinit var timer5Min: CountDownTimer
    lateinit var redirect: ImageButton
    lateinit var db : FirebaseFirestore
    var projects : List<String> = emptyList()
    var tasks : List<String> = emptyList()
    val totalTime = 1500000L // 25 minutes in milliseconds
    val totalTime5Min = 300000L // 5 minutes in milliseconds

    //navigation components
    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        progressBar25Min = findViewById(R.id.progressBar)
        tvTimer25Min = findViewById(R.id.tvTimer)
        progressBar5Min = findViewById(R.id.progressBar5Min)
        tvTimer5Min = findViewById(R.id.tvTimer5Min)
        selectedAProject = findViewById(R.id.spinProjects)
        selectedATask = findViewById(R.id.spinTasks)

        settingsButton = findViewById(R.id.navbarSettings)
        settingsButton.setOnClickListener{
            var KtoEIntent = Intent(this, SettingsActivity::class.java)
            startActivity(KtoEIntent)
        }

        filterButton = findViewById(R.id.navbarFilter)
        filterButton.setOnClickListener{
            var filterIntent = Intent(this, FilterInformationActivty::class.java)
            startActivity(filterIntent)
        }

        homeButton = findViewById(R.id.navbarHome)
        homeButton.setOnClickListener{
            var homeIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homeIntent)
        }

        projectButton = findViewById(R.id.navbarEntries)
        projectButton.setOnClickListener{
            var homeIntent = Intent(this, ViewProjectsActivity::class.java)
            startActivity(homeIntent)
        }

        timer5Min = object : CountDownTimer(0, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {}
        }

        val user = Firebase.auth.currentUser
        val userId = user?.uid
        val TimeEntrydb = FirebaseFirestore.getInstance()

        fetchProjects(userId)
        fetchTasks(userId)
        var startTime = getCurrentTime()
        val startSessionbutton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)
        startSessionbutton.setOnClickListener {
            startTime = getCurrentTime()
            startSession()
        }
        stopButton.setOnClickListener {
            stopSession(startTime)
        }

        redirect = findViewById(R.id.btnTasksRedirect)
        redirect.setOnClickListener{
            var AddTasksIntent = Intent(this, AddATaskActivity::class.java)
            startActivity(AddTasksIntent)
        }

    }

    fun fetchTasks(userID: String?) {
        db = FirebaseFirestore.getInstance()
        val taskref = db.collection("task")
        taskref
            .whereEqualTo("firebaseUUID", userID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val tname = document.getString("tname") ?: ""
                    taskList.add(tname)
                }
                tasks = taskList
                populateTask(taskList)
            }
    }

    fun populateTask(taskList: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectedATask.adapter = adapter
    }

    fun fetchProjects(userID: String?) {
        db = FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref
            .whereEqualTo("firebaseUUID", userID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val projectList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val pname = document.getString("pname") ?: ""
                    projectList.add(pname)
                }
                projects = projectList // Update projects with fetched project names
                populateprojectSpinner(projectList)
                selectedAProject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //val chosenProject = projects[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
    }

    fun populateprojectSpinner(projectList: List<String>)
    {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectedAProject.adapter = adapter

    }

    private fun startSession() {
        startTimer25Min()
    }

    private fun stopSession(startTime: String) {
        timer25Min.cancel()
        if (timer5Min != null) {
            timer5Min.cancel()
        }

        val user = Firebase.auth.currentUser
        val userId = user?.uid
        val db = Firebase.firestore
        val currentDate = getCurrentDate()
        val selectedTask = selectedATask.selectedItem.toString()
        val selectedProject = selectedAProject.selectedItem.toString()


        // Create a new time entry object
        val timeEntry = TimeEntry(
            currentDate,
            userId,
            startTime, // You need to set startTime somewhere before this
            getCurrentTime(), // You need to implement getCurrentTime() function
            selectedTask,
            selectedProject
        )

        // Add the time entry to Firestore
        db.collection("time_entries")
            .add(timeEntry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Time Entry Added Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Time Entry was not added ", Toast.LENGTH_SHORT).show()
            }
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        return formatter.format(currentDate)
    }

    fun getCurrentTime(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return formatter.format(currentTime)
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

        val elapsedTime = totalTime - secondsRemaining * 1000
        val progress = ((elapsedTime.toFloat() / totalTime) * 100).toInt()
        progressBar.progress = progress

        //val progress = (totalTime - secondsRemaining * 1000).toInt()
        //progressBar.progress = progress
    }

}