package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ViewATimeEntryActivity : AppCompatActivity() {
    lateinit var backBtn : FloatingActionButton
    lateinit var ProjectName: EditText
    lateinit var TaskName: EditText
    lateinit var STime: EditText
    lateinit var ETime: EditText

    //navigation components
    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_atime_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //getting TimeEntry object from intent
        val timeEntry = intent.getParcelableExtra<TimeEntryHomeDisplay>("timeEntry")

        ProjectName = findViewById(R.id.txtPName)
        TaskName = findViewById(R.id.txtTName)
        STime = findViewById(R.id.viewStartTime)
        ETime = findViewById(R.id.viewEndTime)

        //settings navigation code - R
        settingsButton = findViewById(R.id.navbarSettings)
        settingsButton.setOnClickListener{
            var KtoEIntent = Intent(this, SettingsActivity::class.java)
            startActivity(KtoEIntent)
        }

        timerButton = findViewById(R.id.navbarPomodoro)
        timerButton.setOnClickListener{
            var timerIntent = Intent(this, PomodoroActivity::class.java)
            startActivity(timerIntent)
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


        if (timeEntry != null) {
            // Populate EditText fields with the TimeEntry object
            ProjectName.setText(timeEntry.entryProject)
            TaskName.setText(timeEntry.selectedTask)
            STime.setText(timeEntry.startTime)
            ETime.setText(timeEntry.endTime)
        } else {
            // Handle case where timeEntry is null (optional)
            Toast.makeText(this, "Error: Time entry not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        backBtn = findViewById(R.id.ViewEntryBackfloatingButton)
        backBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, HomePageActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }
    }

}