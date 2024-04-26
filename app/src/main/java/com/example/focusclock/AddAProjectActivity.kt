package com.example.focusclock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AddAProjectActivity : AppCompatActivity() {
    lateinit var projectName : EditText
    lateinit var duedate : EditText
    lateinit var goalhrs : EditText
    lateinit var saveprojectBtn : Button
    lateinit var gobackBtn : Button

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
    }


}