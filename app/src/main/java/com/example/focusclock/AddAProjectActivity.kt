package com.example.focusclock

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class AddAProjectActivity : AppCompatActivity() {
    lateinit var projectName: EditText
    lateinit var duedate: EditText
    lateinit var goalhrs: EditText
    lateinit var saveprojectBtn: Button
    lateinit var gobackBtn: FloatingActionButton

    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var projectButton: ImageButton

    private val projectDB = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_aproject)
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

        projectName = findViewById(R.id.APprojectNametxt)
        duedate = findViewById(R.id.APDueDatetxt)
        goalhrs = findViewById(R.id.APGoalHrstxt)
        saveprojectBtn = findViewById(R.id.APSaveBtn)
        gobackBtn = findViewById(R.id.APfloatingBackButton)

        duedate.setOnClickListener{
            showDatePickerDialog()
        }

        saveprojectBtn.setOnClickListener {
            // val userId = FirebaseAuth.getInstance().currentUser?.uid
            val user = Firebase.auth.currentUser
            val userId = user?.uid
            if(userId!=null)
            {
                createProject(userId)
                val intent = Intent(this, ViewProjectsActivity::class.java)
                startActivity(intent)
            }


        }
        gobackBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, ViewProjectsActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

        duedate.setOnClickListener{
            showDatePickerDialog()
        }


    }
    fun createProject(userId: String)
    {
        val pname = projectName.text.toString()
        var ddate = duedate.text.toString()
        val ghrs = goalhrs.text.toString()

        if(pname.isEmpty() || ghrs.isEmpty())
        {
            Toast.makeText(this, "Please Fill In All Necessary Project Details", Toast.LENGTH_SHORT).show()
            return
        }
        if (ddate.isNullOrEmpty())
        {
            ddate = "null"
        }
        val newProject = Project(
            projectID = "",
            firebaseUUID = userId,
            pname = pname,
            ddate = ddate,
            ghrs = ghrs
        )

        projectDB.collection("projects")
            .add(newProject)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Project Added Successfully", Toast.LENGTH_SHORT).show()
            }

        projectDB.collection("projects")
            .whereEqualTo("firebaseUUID", userId)
            .whereEqualTo("pname", pname)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentId = document.id
                    val newData = mapOf(
                        "ddate" to ddate,
                        "firebaseUUID" to userId,
                        "ghrs" to ghrs,
                        "pname" to pname,
                        "projectID" to documentId // Using document.id to store the Firestore document ID
                    )
                    projectDB.collection("projects").document(documentId)
                        .update(newData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "ProjectID Updated Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating ProjectID: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {_, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear)
            duedate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}