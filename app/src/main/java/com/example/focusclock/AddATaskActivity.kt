package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class AddATaskActivity : AppCompatActivity() {
    lateinit var taskname: EditText
    lateinit var taskDescription: EditText
    lateinit var selectAProject: Spinner
    lateinit var saveTaskBtn: Button
    lateinit var backFromAddTask: FloatingActionButton
    lateinit var projects : List<Project>

    private val Taskdb = FirebaseFirestore.getInstance()
    // db may be redundant, but would rather use in case of confusion leading to loss of data
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
        saveTaskBtn.isEnabled = false

        val user = Firebase.auth.currentUser
        val userId = user?.uid

        fetchFireStoreProjects(userId)


        saveTaskBtn.setOnClickListener {
            // val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {

                createTask(userId)
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }
        }
        backFromAddTask.setOnClickListener{
            var returnLoginIntent = Intent(this, HomePageActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

    }
    fun populateprojectSpinner(projectList: List<String>)
    {
        //val projectName = projects.map { it.pname }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectAProject.adapter = adapter

    }
    fun fetchFireStoreProjects(userID: String?)
    {
        db=FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref
            .whereEqualTo("firebaseUUID", userID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val projectList = mutableListOf<String>()
                for (document in querySnapshot.documents) {

                    val pname = document.getString("pname")?: ""


                    projectList.add(pname)
                }
                //projects = projectList
                populateprojectSpinner(projectList)
                saveTaskBtn.isEnabled = true
            }
    }
    fun createTask(userId: String)
    {
        val tname = taskname.text.toString()
        val tdescription = taskDescription.text.toString()

        if(tname.isEmpty() || tdescription.isEmpty())
        {
            Toast.makeText(this, "Please Fill In All Necessary Task Details", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedProject = projects[selectAProject.selectedItemPosition].toString()
        val task = Task(
            firebaseUUID = userId,
            tname = tname,
            tdescription = tdescription,
            selectedproject = selectedProject
        )
        Taskdb.collection("task")
            .add(task)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()

            }

    }
}