package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewProjectsActivity : AppCompatActivity() {
    lateinit var addProject : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_projects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addProject = findViewById(R.id.floatActButVPAddProj)
        addProject.setOnClickListener{
            var returnLoginIntent = Intent(this, AddAProjectActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }
    }
}