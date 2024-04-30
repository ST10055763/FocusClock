package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    //variable declarations
    private lateinit var newEmail : EditText
    private lateinit var newFullname : EditText
    private lateinit var newPhone : EditText
    private lateinit var newMin : EditText
    private lateinit var newMax : EditText
    private lateinit var btnUpdate : Button
    private lateinit var HomeButton : ImageButton //- R
    private lateinit var Username : TextView
    private lateinit var LLogout : Button

    private lateinit var settingsButton : ImageButton //- R
    private lateinit var timerButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var projectButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        //settings navigation code - R
        HomeButton = findViewById(R.id.navbarHome)

        HomeButton.setOnClickListener{
            var KtoEIntent = Intent(this, HomePageActivity::class.java)
            startActivity(KtoEIntent)
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

        projectButton = findViewById(R.id.navbarEntries)
        projectButton.setOnClickListener{
            var homeIntent = Intent(this, ViewProjectsActivity::class.java)
            startActivity(homeIntent)
        }

        //initialising in button so new values are updated
        newEmail = findViewById(R.id.edtEmail)
        newFullname = findViewById(R.id.edtFullname)
        newPhone = findViewById(R.id.edtNumber)
        newMin = findViewById(R.id.edtMin)
        newMax = findViewById(R.id.edtMax)
        Username = findViewById(R.id.txtViewUsername)

        //setting the object for firebase and retrieving the current user's id
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        populateUserData(uid, db)

        btnUpdate = findViewById(R.id.btnSave)

        btnUpdate.setOnClickListener {

            //calling update method
            updateUser(uid)
        }

    }

    private fun populateUserData(uid: String?, db: FirebaseFirestore) {

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

    private fun updateUser(uid: String?) {
        val db = FirebaseFirestore.getInstance()

        uid?.let { uid ->
            val userRef = db.collection("profiles").document(uid)

            // Retrieve text from EditText fields
            val email = newEmail.text.toString()
            val fullname = newFullname.text.toString()
            val phone = newPhone.text.toString()
            val minGoals = newMin.text.toString()
            val maxGoals = newMax.text.toString()

            val updates = hashMapOf<String, Any>(
                "email" to email,
                "fname" to fullname,
                "maxgoals" to maxGoals,
                "mingoals" to minGoals,
                "phoneNum" to phone
            )

            userRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this@SettingsActivity, "User details updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@SettingsActivity, "Failed to update user details: ", Toast.LENGTH_SHORT).show()
                }
        }
    }
}