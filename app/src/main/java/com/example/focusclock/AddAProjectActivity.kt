package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddAProjectActivity : AppCompatActivity() {
    lateinit var projectName: EditText
    lateinit var duedate: EditText
    lateinit var goalhrs: EditText
    lateinit var saveprojectBtn: Button
    lateinit var gobackBtn: Button

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
        projectName = findViewById(R.id.APprojectNametxt)
        duedate = findViewById(R.id.APDueDatetxt)
        goalhrs = findViewById(R.id.APGoalHrstxt)
        saveprojectBtn = findViewById(R.id.APSaveBtn)
        gobackBtn = findViewById(R.id.APfloatingBackButton)

        saveprojectBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
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


    }
    fun createProject(userId: String)
    {
        val pname = projectName.text.toString()
        val ddate = duedate.text.toString()
        val ghrs = goalhrs.text.toString()

        if(pname.isEmpty() || ddate.isEmpty() || ghrs.isEmpty())
        {
            Toast.makeText(this, "Please fill in all project details", Toast.LENGTH_SHORT).show()
            return
        }
        val newProject = Project(
            pname = pname,
            ddate = ddate,
            ghrs = ghrs
        )

        projectDB.collection("projects")
            .add(newProject)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Project Added Successfully", Toast.LENGTH_SHORT).show()
            }
    }
}