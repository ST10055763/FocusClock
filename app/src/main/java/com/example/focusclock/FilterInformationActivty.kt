package com.example.focusclock

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

class FilterInformationActivty : AppCompatActivity() {

    // Define your Firestore instance
    private val db = FirebaseFirestore.getInstance()

    private lateinit var selectionSpinner : Spinner
    private lateinit var searchButton : FloatingActionButton
    private lateinit var etStartDate : EditText
    private lateinit var etEndDate : EditText
    private lateinit var recViewEntries : RecyclerView
    private lateinit var backingDate : ImageView

    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton

    //private val projects = mutableListOf<ProjectDisplay>()
    private val timeentries = mutableListOf<TimeEntryFilterDisplay>()
    private lateinit var recadapter: FilterInformationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter_information_activty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        selectionSpinner = findViewById(R.id.spinnerFPFilterBy)

        // Create an ArrayAdapter using the string array and a default spinner layout
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter.add("Date")
        adapter.add("Project")

        selectionSpinner.setAdapter(adapter)

        etStartDate = findViewById(R.id.edtFIStartDate)
        etEndDate = findViewById(R.id.edtFIEndDate)
        backingDate = findViewById(R.id.ivFIDateBack)

        selectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // Check which option is selected
                when (selectedItem) {
                    "Date" -> {
                        // Show date related form components
                        etStartDate.visibility = View.VISIBLE
                        etEndDate.visibility = View.VISIBLE
                        backingDate.visibility = View.VISIBLE
                    }
                    "Project" -> {
                        // Show project related form components
                        etStartDate.visibility = View.GONE
                        etEndDate.visibility = View.GONE
                        backingDate.visibility = View.GONE
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }

        etStartDate.setOnClickListener{
            showDatePickerDialogStart()
        }

        etEndDate.setOnClickListener{
            showDatePickerDialogEnd()
        }

        searchButton = findViewById(R.id.floatActBtnFISearch)
        recViewEntries = findViewById(R.id.recViewFIEntries)

        recadapter = FilterInformationAdapter(timeentries)
        recViewEntries.adapter = recadapter

        // Set a layout manager (e.g., LinearLayoutManager)
        recViewEntries.layoutManager = LinearLayoutManager(this)

        // Add ItemDecoration with desired spacing
        val itemDecoration = SpaceItemDecoration(spaceHeight = resources.getDimensionPixelSize(R.dimen.item_spacing))
        recViewEntries.addItemDecoration(itemDecoration)

        val user = Firebase.auth.currentUser
        val userId = user?.uid

        // fetch data here
        //fetchAndPopulateFireStoreHomeEntries(firebaseUUID, currentDate)

        searchButton.setOnClickListener {
            // Get the selected item from the spinner
            val selectedItem = selectionSpinner.selectedItem.toString()

            // Check which option is selected
            when (selectedItem) {
                "Date" -> {

                    if (etStartDate.text.isNullOrEmpty() || etEndDate.text.isNullOrEmpty()) {
                        Toast.makeText(this, "Please enter a valid range", Toast.LENGTH_SHORT).show()
                    } else {
                        val startDateText = etStartDate.text.toString()
                        val endDateText = etEndDate.text.toString()

                        val startDate = parseDate(startDateText)
                        val endDate = parseDate(endDateText)

                        if (startDate != null && endDate != null) {
                            if (endDate.after(startDate)) {

                                // Pass the correct string dates to the function
                                // fetchAndPopulateFireStoreDateEntries(userId, startDateText, endDateText)
                                fetchAndPopulateFireStoreDateEntries2(userId, startDateText, endDateText)
                            } else {
                                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                "Project" -> {
                    fetchAndPopulateFireStoreProjects(userId)
                }
            }
        }

    }

    private fun parseDate(dateString: String): Date? {
        val formatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        return try {
            formatter.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }



    private fun showDatePickerDialogStart() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {_, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedMonth + 1, selectedDay, selectedYear)
            etStartDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showDatePickerDialogEnd() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {_, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedMonth + 1, selectedDay, selectedYear)
            etEndDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun fetchAndPopulateFireStoreDateEntries(userID: String?, startDate: String, endDate: String) {
        val entriesRef = db.collection("time_entries")

        val dateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val startDateDate = dateFormatter.parse(startDate)
        val endDateDate = dateFormatter.parse(endDate)

        if (startDateDate != null && endDateDate != null) {
            val startDateTimestamp = com.google.firebase.Timestamp(startDateDate)
            val endDateTimestamp = com.google.firebase.Timestamp(endDateDate)

            entriesRef
                .whereEqualTo("firebaseUUID", userID)
                .whereGreaterThanOrEqualTo("currentDate", startDate)
                .whereLessThanOrEqualTo("currentDate", endDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val timeentries = mutableListOf<TimeEntryFilterDisplay>() // Create a new list to avoid duplicates
                    for (document in querySnapshot.documents) {
                        val firebaseUUID = document.getString("firebaseUUID") ?: ""
                        val startTimeString = document.getString("startTime") ?: ""
                        val endTimeString = document.getString("endTime") ?: ""
                        val selectedTask = document.getString("selectedTask") ?: ""
                        val entryProject = document.getString("entryProject") ?: ""
                        val timeEntryPicRef = document.getString("timeEntryPicRef") ?: ""
                        val dateentry = document.getString("currentDate") ?: ""

                        val currentEntry = TimeEntryFilterDisplay(firebaseUUID, startTimeString, endTimeString, selectedTask, entryProject, timeEntryPicRef, dateentry, "")

                        // Define the date format for parsing
                        val dateFormatWithTime = SimpleDateFormat("MM-dd-yyyy HH:mm")
                        val dateFormatWithTimeAndSeconds = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")

                        // Parse start and end times based on their formats
                        val startTime = if (startTimeString.length == 5) {
                            dateFormatWithTime.parse("01-01-2024 $startTimeString")
                        } else {
                            dateFormatWithTimeAndSeconds.parse("01-01-2024 $startTimeString")
                        }
                        val endTime = if (endTimeString.length == 5) {
                            dateFormatWithTime.parse("01-01-2024 $endTimeString")
                        } else {
                            dateFormatWithTimeAndSeconds.parse("01-01-2024 $endTimeString")
                        }

                        // Calculate the duration between start time and end time in milliseconds
                        val durationMillis = endTime.time - startTime.time

                        // Convert duration from milliseconds to hours, minutes, and seconds
                        val hours = durationMillis / (1000 * 60 * 60)
                        val minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60)
                        val seconds = ((durationMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000

                        // Format the duration as hh:mm:ss
                        val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                        currentEntry.durationTask = formattedDuration

                        timeentries.add(currentEntry)

                        Log.d("DocumentFields", "FirebaseUUID: $firebaseUUID, StartTime: $startTimeString. EndTime: $endTimeString ")
                    }

                    if (timeentries.isEmpty()) {
                        Toast.makeText(this, "No tasks found within the selected date range", Toast.LENGTH_SHORT).show()
                    }

                    // After fetching data, update the RecyclerView adapter with the new data
                    recadapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching tasks: ", Toast.LENGTH_SHORT).show()
                }
        }
        else
        {
            Toast.makeText(this, "Failed to parse start or end date: ", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun fetchAndPopulateFireStoreDateEntries2(userID: String?, startDate: String, endDate: String) {
        val entriesRef = db.collection("time_entries")
        val dateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val startDateDate = dateFormatter.parse(startDate)
        val endDateDate = dateFormatter.parse(endDate)

        if (startDateDate != null && endDateDate != null) {
            entriesRef
                .whereEqualTo("firebaseUUID", userID)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val dateEntryString = document.getString("currentDate") ?: ""
                        val dateEntryDate = dateFormatter.parse(dateEntryString)

                        if (dateEntryDate >= startDateDate && dateEntryDate <= endDateDate) {
                            val firebaseUUID = document.getString("firebaseUUID") ?: ""
                            val startTimeString = document.getString("startTime") ?: ""
                            val endTimeString = document.getString("endTime") ?: ""
                            val selectedTask = document.getString("selectedTask") ?: ""
                            val entryProject = document.getString("entryProject") ?: ""
                            val timeEntryPicRef = document.getString("timeEntryPicRef") ?: ""

                            val currentEntry = TimeEntryFilterDisplay(firebaseUUID, startTimeString, endTimeString, selectedTask, entryProject, timeEntryPicRef, dateEntryString, "")

                            val dateFormatWithTime = SimpleDateFormat("MM-dd-yyyy HH:mm")
                            val dateFormatWithTimeAndSeconds = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")

                            val startTime = if (startTimeString.length == 5) {
                                dateFormatWithTime.parse("01-01-2024 $startTimeString")
                            } else {
                                dateFormatWithTimeAndSeconds.parse("01-01-2024 $startTimeString")
                            }
                            val endTime = if (endTimeString.length == 5) {
                                dateFormatWithTime.parse("01-01-2024 $endTimeString")
                            } else {
                                dateFormatWithTimeAndSeconds.parse("01-01-2024 $endTimeString")
                            }

                            val durationMillis = endTime.time - startTime.time
                            val hours = durationMillis / (1000 * 60 * 60)
                            val minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60)
                            val seconds = ((durationMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000
                            val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                            currentEntry.durationTask = formattedDuration
                            timeentries.add(currentEntry)
                        }
                    }
                    // After fetching data, update the RecyclerView adapter with the new data
                    recadapter.notifyDataSetChanged()

                    if (timeentries.isEmpty()) {
                        Toast.makeText(this, "No tasks found within the selected date range", Toast.LENGTH_SHORT).show()
                    }

                    recadapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching tasks: $it", Toast.LENGTH_SHORT).show()
                }
        }
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

                    fetchSpecificProjectEntries(userID, project)
                }
                // After fetching data, notify the adapter of the change
                recadapter.notifyDataSetChanged()
            }
    }

    private fun fetchSpecificProjectEntries(userID: String?, project: ProjectDisplay) {
        var totalTasks: Int = 0
        var totalHours: Double = 0.0 // Assuming you want to store hours as a double

        val db = FirebaseFirestore.getInstance()
        val entriesref = db.collection("time_entries")
        entriesref
            .whereEqualTo("firebaseUUID", userID)
            .whereEqualTo("entryProject", project.pname)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {

                    val firebaseUUID = document.getString("firebaseUUID") ?: ""
                    val startTimeString = document.getString("startTime") ?: ""
                    val endTimeString = document.getString("endTime") ?: ""
                    val selectedTask = document.getString("selectedTask") ?: ""
                    val entryProject = document.getString("entryProject") ?: ""
                    val timeEntryPicRef = document.getString("timeEntryPicRef") ?: ""
                    val dateentry = document.getString("currentDate") ?: ""

                    val currentEntry = TimeEntryFilterDisplay(firebaseUUID, startTimeString, endTimeString, selectedTask, entryProject, timeEntryPicRef, dateentry, "")

                    // Convert start time and end time to Date objects
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

                    // Convert duration from milliseconds to hours
                    //val hours = durationMillis.toDouble() / (1000 * 60 * 60)

                    //currentEntry.durationTask = hours

                    // Convert duration from milliseconds to hours, minutes, and seconds
                    val hours = durationMillis / (1000 * 60 * 60)
                    val minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60)
                    val seconds = ((durationMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000

                    // Format the duration as hh:mm:ss
                    val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    currentEntry.durationTask = formattedDuration

                    timeentries.add(currentEntry)
                }
                // After fetching data, notify the adapter of the change
                recadapter.notifyDataSetChanged()
            }
    }

}