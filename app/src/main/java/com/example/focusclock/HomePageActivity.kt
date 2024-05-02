package com.example.focusclock

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
import java.util.Calendar
import java.util.Locale

class HomePageActivity : AppCompatActivity(), HomePageAdapter.OnItemClickListener {

    // Define your Firestore instance
    private val db = FirebaseFirestore.getInstance()

    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton
    private lateinit var addTimeEntryButton: FloatingActionButton

    private lateinit var tvDateHeader : TextView
    private lateinit var tvUserHeader: TextView
    private lateinit var tvHomeHours: TextView
    private lateinit var btnMinGoals: Button
    private lateinit var btnMaxGoals: Button
    private lateinit var tvHomeTasksDone: TextView

    private lateinit var timeentriesRecyclerView: RecyclerView
    private val timeentries = mutableListOf<TimeEntryHomeDisplay>()
    private lateinit var adapter: HomePageAdapter

    // Define mutable variables for tasksDone and hoursToday
    private var tasksDone: Int = 0
    private var hoursToday: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        addTimeEntryButton = findViewById(R.id.floatABHomeAdd)
        addTimeEntryButton.setOnClickListener{
            var addTimeIntent = Intent(this, AddATimeEntryActivity::class.java)
            startActivity(addTimeIntent)
        }

        var maxHours: Int = 0
        var minHours: Int = 0

        // Retrieve firebaseUUID from Intent extras
        //val firebaseUUID = intent.getStringExtra("firebaseUUID")
        val user = Firebase.auth.currentUser
        val firebaseUUID = user?.uid

        // Retrieve current date
        val currentDate = getCurrentDate()

        // CODE HERE
        timeentriesRecyclerView = findViewById(R.id.recviewHomeTimeLogs)
        adapter = HomePageAdapter(timeentries)
        timeentriesRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(this)

        // Set a layout manager (e.g., LinearLayoutManager)
        timeentriesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add ItemDecoration with desired spacing
        val itemDecoration = SpaceItemDecoration(spaceHeight = resources.getDimensionPixelSize(R.dimen.item_spacing))
        timeentriesRecyclerView.addItemDecoration(itemDecoration)

        // fetch data here
        fetchAndPopulateFireStoreHomeEntries(firebaseUUID, currentDate)
        //hoursToday = String.format("%.3f", hoursToday).toDouble()

        tvDateHeader = findViewById(R.id.tvHomePageDateHeader)
        tvDateHeader.setText("Here's Todays Schedule " + currentDate)

        tvUserHeader = findViewById(R.id.tvHomePageUHeader)
        tvHomeHours = findViewById(R.id.tvHomeHoursDone)
        tvHomeTasksDone = findViewById(R.id.tvHomeTasksComplete)

        // Retrieve the document from Firestore based on userId
        if (firebaseUUID != null) {
            db.collection("profiles")
                .document(firebaseUUID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Document exists, retrieve the "fname" field
                        val fname = document.getString("fname")
                        maxHours = document.getString("maxgoals")?.toInt() ?: 0
                        minHours = document.getString("mingoals")?.toInt() ?: 0
                        if (fname != null) {
                            // fname is not null, you can use it
                            tvUserHeader.setText("Hello, " + fname)
                            tvHomeHours.setText("${hoursToday} / ${minHours} Hours Done Today")
                            tvHomeTasksDone.setText("${tasksDone} Tasks Completed Today")

                        } else {
                            // fname is null, handle appropriately
                            Toast.makeText(this, "Failed to retrieve first name", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Document does not exist
                        Toast.makeText(this, "User profile does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failures
                    Toast.makeText(this, "Failed to retrieve user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // SORT OUT
        btnMinGoals = findViewById(R.id.btnHomeMinGoal)
        btnMaxGoals = findViewById(R.id.btnHomeMaxGoal)

        btnMinGoals.setOnClickListener{
            tvHomeHours.setText("${hoursToday} / ${minHours} Hours Done Today")
        }
        btnMaxGoals.setOnClickListener{
            tvHomeHours.setText("${hoursToday} / ${maxHours} Hours Done Today")
        }

    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onItemClick(timeEntry: TimeEntryHomeDisplay) {
        val intent = Intent(this, ViewATimeEntryActivity::class.java)
        // Pass the TimeEntry object as an extra with the intent
        intent.putExtra("timeEntry", timeEntry)
        startActivity(intent)
    }

    private fun fetchAndPopulateFireStoreHomeEntries(userID: String?, currentDate: String)
    {
        val entriesref = db.collection("time_entries")
        entriesref
            .whereEqualTo("firebaseUUID", userID)
            .whereEqualTo("currentDate", currentDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    tasksDone += 1

                    val firebaseUUID = document.getString("firebaseUUID") ?: ""
                    val startTimeString = document.getString("startTime") ?: ""
                    val endTimeString = document.getString("endTime") ?: ""
                    val selectedTask = document.getString("selectedTask") ?: ""
                    val entryProject = document.getString("entryProject") ?: ""
                    val timeEntryPicRef = document.getString("timeEntryPicRef") ?: ""
                    val dateentry = document.getString("currentDate") ?: ""

                    val currentEntry = TimeEntryHomeDisplay(firebaseUUID, startTimeString, endTimeString, selectedTask, entryProject, timeEntryPicRef, dateentry, 0.0)

                    // Convert start time and end time to Date objects
                    val dummyDate = "1970-01-01 "
                    val startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + startTimeString)
                    val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummyDate + endTimeString)

                    // Calculate the duration between start time and end time in milliseconds
                    val durationMillis = endTime.time - startTime.time

                    // Convert duration from milliseconds to hours
                    val hours = durationMillis.toDouble() / (1000 * 60 * 60)

                    // Update total hours
                    hoursToday += hours
                    //currentEntry.durationTask = hours
                    currentEntry.durationTask = String.format("%.3f", hours).toDouble()

                    // Format hoursToday here
                    hoursToday = String.format("%.3f", hoursToday).toDouble()

                    timeentries.add(currentEntry)
                }

                // Update UI after processing all documents
                tvHomeHours.setText("${hoursToday} / x Done Today")
                tvHomeTasksDone.setText("${tasksDone} Tasks Completed Today")
                // After fetching data, notify the adapter of the change
                adapter.notifyDataSetChanged()
            }
    }

}