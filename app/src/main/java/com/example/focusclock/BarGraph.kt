package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.lang.RuntimeException

class BarGraph : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var firestore: FirebaseFirestore
    private lateinit var returnHome: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bar_graph)

        //variables
        barChart = findViewById(R.id.bar_chart)
        firestore = FirebaseFirestore.getInstance()

        returnHome = findViewById(R.id.returnHomeFloatBtn)
        returnHome.setOnClickListener{
            //add redirection to kiashen's line graph page here
            var goHomeIntent = Intent(this, HomePageActivity::class.java)
            startActivity(goHomeIntent)
        }

        val user = Firebase.auth.currentUser
        val userId = user?.uid

        //calendar
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = dateFormat.format(calendar.time)

        //calling the method
        fetchStudyData(userId, firstDate, currentDate)
    }

    //methods

    //method 1 -> Fetching data from firebase
    private fun fetchStudyData(userID : String?, startDate: String, endDate: String) {
        Log.d("FETCH_DATA", "Fetching data from Firebase...")

        val startDateObject = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).parse(startDate)
        val endDateObject = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).parse(endDate)

        //retrieving collection
        val dailyRef = firestore.collection("daily_entries")

        firestore.collection("daily_entries")
            .whereEqualTo("firebaseUUID", userID)
            //.whereGreaterThanOrEqualTo("currentDate", startDateObject)
            //.whereLessThanOrEqualTo("currentDate", endDateObject)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FETCH_DATA", "Data retrieved successfully. Document count: ${querySnapshot.documents.size}")
                //Log.d("FETCH_DATA", "Data retrieved successfully. Document count: ${documents.size()}")
                val studyData = mutableListOf<StudyEntry>()
                var minGoal = 0
                var maxGoal = 0

                querySnapshot.documents.forEachIndexed { index, document ->
                    try {
                        val currentDate = document.getString("currentDate") ?: ""
                        val firebaseUUID = document.getString("firebaseUUID") ?: ""
                        val maxgoal = document.getString("maxgoal") ?: "0"
                        val mingoal = document.getString("mingoal") ?: "0"
                        val totalHours = document.getString("totalHours") ?: "0"
                        val tHours = totalHours.toFloatOrNull() ?: 0.0f

                        //if statement to check the date for current month
                        if (currentDate >= startDate && currentDate <= endDate) {
                            val currentEntry = DailyEntry(currentDate, maxgoal, mingoal, totalHours)
                            studyData.add(StudyEntry(currentDate, tHours))
                        }

                        minGoal = mingoal.toInt()
                        maxGoal = maxgoal.toInt()
                    } catch (e: RuntimeException) {
                        Log.e("DataError", "Skipping document with incorrect format ${document.id}", e)
                    }
                }

                displayBarChart(studyData, minGoal, maxGoal)
            }
            .addOnFailureListener { exception ->
                Log.e("FETCH_DATA", "Error fetching data: ${exception.message}")
            }
    }

    //method 2 -> display grpah and graph design specifications
    private fun displayBarChart(studyData: List<StudyEntry>, minGoal: Int, maxGoal: Int) {
        Log.d("DISPLAY_CHART", "Displaying bar chart with ${studyData.size} data points.")
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        for (i in studyData.indices) {
            val date = studyData[i].date
            val hours = studyData[i].hours
            entries.add(BarEntry(i.toFloat(), hours))
            date?.let { labels.add(it) }
        }

        val dataSet = BarDataSet(entries, "Study Hours")
        val data = BarData(dataSet)

        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f

        val leftAxis = barChart.axisLeft
        leftAxis.addLimitLine(LimitLine(minGoal.toFloat(), "Min Goal").apply {
            lineColor = Color.RED
            lineWidth = 2f
            textColor = Color.BLACK
            textSize = 12f
        })
        leftAxis.addLimitLine(LimitLine(maxGoal.toFloat(), "Max Goal").apply {
            lineColor = Color.GREEN
            lineWidth = 2f
            textColor = Color.BLACK
            textSize = 12f
        })

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.isEnabled = true
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)

        barChart.invalidate()
    }

    //data class for list to store information for view
    data class StudyEntry(val date: String?, val hours: Float)
}




/* copy of data class -> will remove, here for me to reference
   data class DailyEntry(
    val currentDate: String = "",
    val firebaseUUID: String = "",
    val mingoal: String = "0",
    val maxgoal: String = "0",
    val totalHours: String = "0.0")
*/

/*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/