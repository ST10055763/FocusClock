package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class AddATimeEntryActivity : AppCompatActivity() {
lateinit var backBtn:Button
lateinit var addTaskBtn: Button
lateinit var logBtn: Button
lateinit var timeEntryPic : Button
lateinit var timeEntryProject : Spinner
lateinit var timeEntryTask:Spinner
lateinit var timeEntryStartTime : EditText
lateinit var timeEntryEndTime : EditText
lateinit var t: List<Task>
lateinit var proj: List<Project>

    private val TimeEntrydb = FirebaseFirestore.getInstance()
    // db may be redundant, but would rather use in case of confusion leading to loss of data
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_atime_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backBtn = findViewById(R.id.AEfloatingReturnButton)
        addTaskBtn = findViewById(R.id.floatingAddTaskButton)
        logBtn = findViewById(R.id.AELoginBtn)
        timeEntryProject = findViewById(R.id.spinnerTimeEntProj)
        timeEntryTask = findViewById(R.id.spinnerTimeEntTask)
        timeEntryStartTime = findViewById(R.id.AEStartTimetxt)
        timeEntryEndTime = findViewById(R.id.AEendTimetxt)
        timeEntryPic = findViewById(R.id.AEAddPictureBtn)

        fetchFireStoreProjects()
        fetchFireStoreTasks()

        addTaskBtn.setOnClickListener {
             //val userId = FirebaseAuth.getInstance().currentUser?.uid
            val user = Firebase.auth.currentUser
            val userId = user?.uid
            if (userId != null) {

                createTimeEntry(userId)
                val intent = Intent(this, ViewATimeEntryActivity::class.java)
                startActivity(intent)
            }
        }
        backBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, ViewProjectsActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

    }
    fun populateprojectSpinner()
    {
        val projectName = proj.map { it.pname }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryProject.adapter = adapter

    }
    fun populatetaskSpinner()
    {
        val taskname = t.map { it.tname }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskname)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryTask.adapter = adapter

    }
    fun fetchFireStoreProjects()
    {
        db=FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref.get()
            .addOnSuccessListener { querySnapshot ->
                val projectList = mutableListOf<Project>()
                for (document in querySnapshot.documents) {
                    val firebaseUUID = document.getString("firebaseUUID")
                    val pname = document.getString("pname")?: ""
                    val ddate = document.getString("ddate")?: ""
                    val ghrs = document.getLong("ghrs")?.toInt() ?: 0
                    val project = Project(firebaseUUID,pname, ddate, ghrs)
                    projectList.add(project)
                }
                proj = projectList
                populateprojectSpinner()
                //addTaskBtn.isEnabled = true
            }
    }
    fun fetchFireStoreTasks()
    {
        db = FirebaseFirestore.getInstance()
        val taskref = db.collection("task")
        taskref.get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                for (document in querySnapshot.documents) {
                    val firebaseUUID = document.getString("firebaseUUID")
                    val tname = document.getString("tname")
                    val tdescription = document.getString("tdescription")
                    val projectID = document.getString("selectedproject")
                    //val project = fetchFireStoreProjects(projectID)
                    val project = proj.find { it.firebaseUUID == projectID }
                    val task = Task(firebaseUUID, tname, tdescription, project)
                    taskList.add(task)
                }
                t=taskList
                populatetaskSpinner()
            }
    }
    fun createTimeEntry(userId : String)
    {
        val startTime = timeEntryStartTime.text.toString()
        val endTime = timeEntryEndTime.text.toString()

        if(startTime.isEmpty() || endTime.isEmpty())
        {
            Toast.makeText(this, "Please Fill In All Necessary Time Entry Details", Toast.LENGTH_SHORT).show()
            return
        }
        val entryProject = proj[timeEntryProject.selectedItemPosition]
        val selectedTask = t[timeEntryTask.selectedItemPosition]
        val timeEntry = TimeEntry(
            firebaseUUID = userId,
            startTime = startTime,
            endTime = endTime,
            selectedTask = selectedTask,
            entryProject = entryProject,
            timeEntryPic = AEAddPictureBtn
        )
        TimeEntrydb.collection("timeentry")
            .add(timeEntry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Time Entry Added Successfully", Toast.LENGTH_SHORT).show()

            }

    }



    }




