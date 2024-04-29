package com.example.focusclock

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.UserProfileChangeRequest

class SettingsActivity : AppCompatActivity() {

    //variable declarations
    private lateinit var newEmail : EditText
    private lateinit var newFullname : EditText
    private lateinit var newPhone : EditText
    private lateinit var newMin : EditText
    private lateinit var newMax : EditText
    private lateinit var btnUpdate : Button
    private lateinit var HomeButton : ImageButton //- R

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

        //setting the object for firebase and retrieving the current user's id
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        //initialising
        newEmail = findViewById(R.id.edtEmail)
        newFullname = findViewById(R.id.edtFullname)
        newPhone = findViewById(R.id.edtNumber)
        newMin = findViewById(R.id.edtMin)
        newMax = findViewById(R.id.edtMax)

        btnUpdate.setOnClickListener {
            updateUser(uid)
        }

    }

    private fun updateUser(uid: String?) {
        val db = FirebaseFirestore.getInstance()

        uid?.let { uid ->
            val userRef = db.collection("users").document(uid)

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