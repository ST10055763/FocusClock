package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ViewProjectsActivity : AppCompatActivity() {

    private lateinit var settingsButton: ImageButton
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton
    private lateinit var addTimeEntryButton: FloatingActionButton

    private lateinit var projectsRecyclerView: RecyclerView
    private val projects = mutableListOf<ProjectDisplay>()
    private lateinit var adapter: ViewProjectAdapter

    private lateinit var addProject: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_projects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addProject = findViewById(R.id.floatActButVPAddProj)
        addProject.setOnClickListener {
            val returnLoginIntent = Intent(this, AddAProjectActivity::class.java)
            startActivity(returnLoginIntent)
            finish()
        }

        settingsButton = findViewById(R.id.navbarSettings)
        settingsButton.setOnClickListener {
            val KtoEIntent = Intent(this, SettingsActivity::class.java)
            startActivity(KtoEIntent)
        }

        timerButton = findViewById(R.id.navbarPomodoro)
        timerButton.setOnClickListener {
            val timerIntent = Intent(this, PomodoroActivity::class.java)
            startActivity(timerIntent)
        }

        filterButton = findViewById(R.id.navbarFilter)
        filterButton.setOnClickListener {
            val filterIntent = Intent(this, FilterInformationActivty::class.java)
            startActivity(filterIntent)
        }

        homeButton = findViewById(R.id.navbarHome)
        homeButton.setOnClickListener {
            val homeIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homeIntent)
        }

        projectButton = findViewById(R.id.navbarEntries)
        projectButton.setOnClickListener {
            val homeIntent = Intent(this, ViewProjectsActivity::class.java)
            startActivity(homeIntent)
        }

        projectsRecyclerView = findViewById(R.id.recViewVP)
        adapter = ViewProjectAdapter(projects)
        projectsRecyclerView.adapter = adapter

        // Set a layout manager (e.g., LinearLayoutManager)
        projectsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add ItemDecoration with desired spacing
        val itemDecoration = SpaceItemDecoration(spaceHeight = resources.getDimensionPixelSize(R.dimen.item_spacing))
        projectsRecyclerView.addItemDecoration(itemDecoration)

        val user = Firebase.auth.currentUser
        val userId = user?.uid
        fetchAndPopulateFireStoreProjects(userId)
    }

    private fun fetchAndPopulateFireStoreProjects(userID: String?) {
        val db = FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref
            .whereEqualTo("firebaseUUID", userID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val projectID = document.id
                    val firebaseUUID = document.getString("firebaseUUID") ?: ""
                    val pname = document.getString("pname") ?: ""
                    val ddate = document.getString("ddate") ?: ""
                    // Convert the "ghrs" string to an integer or default to 0 if conversion fails
                    val ghrs = document.getString("ghrs")?.toIntOrNull() ?: 0

                    val project = ProjectDisplay(projectID, firebaseUUID, pname, ddate, ghrs, 0, 0.0)
                    projects.add(project)

                    fetchSpecificProjectEntries(userID, project)
                }
                // After fetching data, notify the adapter of the change
                adapter.notifyDataSetChanged()
            }
    }

    private fun fetchSpecificProjectEntries(userID: String?, project: ProjectDisplay) {
        val db = FirebaseFirestore.getInstance()
        val entriesref = db.collection("time_entries")
        entriesref
            .whereEqualTo("firebaseUUID", userID)
            .whereEqualTo("entryProject", project.pname)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var totalTasks = 0
                var totalHours = 0.0

                for (document in querySnapshot.documents) {
                    // Increment total tasks for the project
                    totalTasks++

                    val startTimeString = document.getString("startTime") ?: ""
                    val endTimeString = document.getString("endTime") ?: ""

                    // Define the date format for parsing
                    val dateFormatWithTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val dateFormatWithTimeAndSeconds = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    // Parse start and end times based on their formats
                    val startTime = if (startTimeString.length == 5) {
                        dateFormatWithTime.parse("2024-01-01 $startTimeString")
                    } else {
                        dateFormatWithTimeAndSeconds.parse("2024-01-01 $startTimeString")
                    }
                    val endTime = if (endTimeString.length == 5) {
                        dateFormatWithTime.parse("2024-01-01 $endTimeString")
                    } else {
                        dateFormatWithTimeAndSeconds.parse("2024-01-01 $endTimeString")
                    }

                    // Calculate the duration between start time and end time in milliseconds
                    val durationMillis = endTime.time - startTime.time

                    // Convert duration from milliseconds to hours and add to total hours for the project
                    val hours = durationMillis.toDouble() / (1000 * 60 * 60)

                    // Update the hours formatting
                    val formattedHours = String.format(Locale.US, "%.3f", hours).toDouble()
                    totalHours += formattedHours
                }

                // Update the project's total hours and tasks done
                project.totTasks = totalTasks
                project.hoursDone = totalHours

                // Notify the adapter of the change
                adapter.notifyDataSetChanged()
            }
    }
}
