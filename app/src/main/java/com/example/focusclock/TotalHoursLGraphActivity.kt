package com.example.focusclock

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TotalHoursLGraphActivity : AppCompatActivity() {

    // Define your Firestore instance
    private val db = FirebaseFirestore.getInstance()

    private lateinit var searchButton: FloatingActionButton
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var lineChart: LineChart

    private var minHours: Int = 0
    private var maxHours: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_total_hours_lgraph)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etStartDate = findViewById(R.id.edtTHLGStartDate)
        etEndDate = findViewById(R.id.edtTHLGEndDate)
        searchButton = findViewById(R.id.floatActBtnTHLGSearch)
        lineChart = findViewById(R.id.lineChart)

        val user = Firebase.auth.currentUser
        val userId = user?.uid

        etStartDate.setOnClickListener {
            showDatePickerDialogStart()
        }

        etEndDate.setOnClickListener {
            showDatePickerDialogEnd()
        }

        searchButton.setOnClickListener {

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
                        retrieveUserProfileAndFetchEntries(userId, startDateText, endDateText)
                    } else {
                        Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
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

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
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

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedMonth + 1, selectedDay, selectedYear)
            etEndDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun retrieveUserProfileAndFetchEntries(userID: String?, startDate: String, endDate: String) {
        userID?.let {
            db.collection("profiles")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        maxHours = document.getString("maxgoals")?.toInt() ?: 0
                        minHours = document.getString("mingoals")?.toInt() ?: 0
                        fetchAndPopulateFireStoreDateEntriesGraph(userID, startDate, endDate)
                    } else {
                        Toast.makeText(this, "User profile does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching user profile: $it", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchAndPopulateFireStoreDateEntriesGraph(userID: String?, startDate: String, endDate: String) {
        val entriesRef = db.collection("daily_entries")
        val dateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val startDateDate = dateFormatter.parse(startDate)
        val endDateDate = dateFormatter.parse(endDate)

        if (startDateDate != null && endDateDate != null) {
            val calendar = Calendar.getInstance()
            val entries = mutableListOf<Entry>()
            val dateLabels = mutableListOf<String>()

            calendar.time = startDateDate

            val totalDays = ((endDateDate.time - startDateDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
            var processedDays = 0

            while (!calendar.time.after(endDateDate)) {
                val currentDate = dateFormatter.format(calendar.time)
                val xValue = (calendar.time.time - startDateDate.time).toFloat() / (1000 * 60 * 60 * 24)
                dateLabels.add(currentDate)

                entriesRef
                    .whereEqualTo("firebaseUUID", userID)
                    .whereEqualTo("currentDate", currentDate)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        var totalHours = 0.0f
                        for (document in querySnapshot.documents) {
                            totalHours += document.getString("totalHours")?.toFloat() ?: 0.0f
                        }

                        entries.add(Entry(xValue, totalHours))
                        processedDays++

                        // Check if all days have been processed
                        if (processedDays == totalDays) {
                            updateLineChart(entries, dateLabels)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error fetching tasks: $it", Toast.LENGTH_SHORT).show()
                        processedDays++

                        // Check if all days have been processed
                        if (processedDays == totalDays) {
                            updateLineChart(entries, dateLabels)
                        }
                    }

                calendar.add(Calendar.DATE, 1)
            }
        }
    }

    private fun updateLineChart(entries: MutableList<Entry>, dateLabels: List<String>) {
        // Ensure that for dates with no entries, we add an entry with totalHours = 0
        for (i in dateLabels.indices) {
            val currentDate = dateLabels[i]
            if (entries.none { it.x == i.toFloat() }) {
                entries.add(Entry(i.toFloat(), 0.0f))
            }
        }

        // Sort the entries by their x-value to avoid issues with the chart renderer
        entries.sortBy { it.x }

        val dataSet = LineDataSet(entries, "Total Hours")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Customizing the x-axis to show dates
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        xAxis.granularity = 1f

        // Highlight min and max goals on y-axis
        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 24f
        yAxis.granularity = 1f // Increment by 1
        yAxis.addLimitLine(createLimitLine(minHours.toFloat(), "Min Goal"))
        yAxis.addLimitLine(createLimitLine(maxHours.toFloat(), "Max Goal"))

        // Disable right y-axis
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()  // Refresh the chart
    }



    private fun createLimitLine(limit: Float, label: String): LimitLine {
        val limitLine = LimitLine(limit, label)
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        limitLine.textSize = 10f
        return limitLine
    }
}
