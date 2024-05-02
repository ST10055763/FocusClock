package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ViewATimeEntryActivity : AppCompatActivity() {
    lateinit var backBtn : FloatingActionButton
    lateinit var ProjectName: EditText
    lateinit var TaskName: EditText
    lateinit var STime: EditText
    lateinit var ETime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_atime_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backBtn = findViewById(R.id.ViewEntryBackfloatingButton)
        backBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, HomePageActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }
    }

    private fun populateEntryData(uid: String?, db: FirebaseFirestore, pname: String, task: String ) {

        if(uid != null)
        {
            val entryRef = db.collection("time_entries")
            entryRef
                .whereEqualTo("firebaseUUID", uid)
                .whereEqualTo("entryProject", pname)
                .whereEqualTo("selectedTask", task)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(TimeEntry::class.java)

                    }
                }
        }

        if (uid != null) {
            val userRef = db.collection("profiles").document(uid)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve user data
                        val userData = documentSnapshot.toObject(Users::class.java)
                        // Populate data into EditText components
                        newEmail.setText(userData?.email)
                        newFullname.setText(userData?.fname)
                        newPhone.setText(userData?.phoneNum)
                        newMin.setText(userData?.mingoals)
                        newMax.setText(userData?.maxgoals)
                        Username.setText(userData?.fname)
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

}