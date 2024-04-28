package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class AddATaskActivity : AppCompatActivity() {
    lateinit var taskname: EditText
    lateinit var taskDescription: EditText
    lateinit var selectAProject: Spinner
    lateinit var saveTaskBtn: Button
    lateinit var backFromAddTask: Button
    lateinit var projects : List<Project>

    private val Taskdb = FirebaseFirestore.getInstance()
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_atask)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskname = findViewById(R.id.ATtaskNametxt)
        taskDescription = findViewById(R.id.ATdescriptiontxt)
        selectAProject = findViewById(R.id.spinnerTSSelectProj)
        saveTaskBtn = findViewById(R.id.ATSavebutton)
        backFromAddTask = findViewById(R.id.ATfloatingBackButton)

        //fun to fetch project lists from FireBase
        fetchFireStoreProjects()



        saveTaskBtn.setOnClickListener {
            // val userId = FirebaseAuth.getInstance().currentUser?.uid
            val user = Firebase.auth.currentUser
            val userId = user?.uid
            if (userId != null) {
                createTask(userId)
                val intent = Intent(this, ViewProjectsActivity::class.java)
                startActivity(intent)
            }
        }
        fun createTask(userId:String)
        {
            val tname = taskname.text.toString()
            val tdescription = taskDescription.text.toString()

        }
        fun populateprojectSpinner()
        {
            val projectName = projects.map { it.pname }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            selectAProject.adapter = adapter

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
                    projects = projectList
                    populateprojectSpinner()
                }
        }

    }
}