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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomePageActivity : AppCompatActivity() {

    // Define your Firestore instance
    private val db = FirebaseFirestore.getInstance()

    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton


    private lateinit var tvDateHeader : TextView
    private lateinit var tvUserHeader: TextView
    private lateinit var edtHomeEnterDate: EditText
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

        // Retrieve firebaseUUID from Intent extras
        val firebaseUUID = intent.getStringExtra("firebaseUUID")

        // Retrieve current date
        val currentDate = getCurrentDate()

        tvDateHeader = findViewById(R.id.tvHomePageDateHeader)
        tvDateHeader.setText("Here's Todays Schedule " + currentDate)

        tvUserHeader = findViewById(R.id.tvHomePageUHeader)

        // Retrieve the document from Firestore based on userId
        if (firebaseUUID != null) {
            db.collection("profiles")
                .document(firebaseUUID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // NOTE: CAN ALSO RETRIEVE MIN AND MAX HOURS HERE
                        // Document exists, retrieve the "fname" field
                        val fname = document.getString("fname")
                        if (fname != null) {
                            // fname is not null, you can use it
                            tvUserHeader.setText("Hello, " + fname)
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

        edtHomeEnterDate = findViewById(R.id.edtHomeEnterDate)

        edtHomeEnterDate.setOnClickListener{
            showDatePickerDialog()
        }

    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {_, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear)
            edtHomeEnterDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}