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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ViewProjectsActivity : AppCompatActivity() {

    private lateinit var settingsButton : ImageButton
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton
    private lateinit var addTimeEntryButton: FloatingActionButton

//    private var totalTasks: Int = 0
//    private var totalHours: Double = 0.0 // Assuming you want to store hours as a double

    private lateinit var projectsRecyclerView: RecyclerView
    private val projects = mutableListOf<ProjectDisplay>()
    private lateinit var adapter: ViewProjectAdapter

    private lateinit var addProject : FloatingActionButton
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
        addProject.setOnClickListener{
            var returnLoginIntent = Intent(this, AddAProjectActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

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
                    //fetchTotalTasksAndHours(userID, project)
//                    totalTasks = 0
//                    totalHours = 0.0
                }
                // After fetching data, notify the adapter of the change
                adapter.notifyDataSetChanged()
            }


    }

//    private fun fetchTotalTasksAndHours(userID: String?, project: ProjectDisplay) {
//
//        val db = FirebaseFirestore.getInstance()
//        val entriesref = db.collection("time_entries")
//        entriesref
//            .whereEqualTo("firebaseUUID", userID)
//            .whereEqualTo("entryProject", project.pname)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot.documents) {
//                    totalTasks += 1
//                    val startTimeString = document.getString("startTime") ?: ""
//                    val endTimeString = document.getString("endTime") ?: ""
//
//                    // Convert start time and end time to Date objects
//                    val dummyDate = "1970-01-01 "
//                    val startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + startTimeString)
//                    val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + endTimeString)
//
//                    // Calculate the duration between start time and end time in milliseconds
//                    val durationMillis = endTime.time - startTime.time
//
//                    // Convert duration from milliseconds to hours
//                    val hours = durationMillis.toDouble() / (1000 * 60 * 60)
//
//                    // Update total hours
//                    totalHours += hours
//                }
//
//                // Update the project object with total tasks and total hours
//                project.totTasks = totalTasks
//                project.hoursDone = totalHours
//            }
//        // After fetching data, notify the adapter of the change
//        adapter.notifyDataSetChanged()
//    }

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

                    // Convert start time and end time to Date objects
                    val dummyDate = "1970-01-01 "
                    val startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + startTimeString)
                    val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + endTimeString)

                    // Calculate the duration between start time and end time in milliseconds
                    val durationMillis = endTime.time - startTime.time

                    // Convert duration from milliseconds to hours and add to total hours for the project
                    // val hours = String.format("%.3f", durationMillis.toDouble() / (1000 * 60 * 60)).toDouble()
                    // totalHours += hours
                    totalHours += durationMillis.toDouble() / (1000 * 60 * 60)
                }

                // Update the project's total hours and tasks done
                project.totTasks = totalTasks
                project.hoursDone = totalHours

                // Notify the adapter of the change
                adapter.notifyDataSetChanged()
            }
    }




}