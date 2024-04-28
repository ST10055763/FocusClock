package com.example.focusclock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SettingsActivity : AppCompatActivity() {

    //variable declarations
    private lateinit var newEmail : EditText
    private lateinit var newFullname : EditText
    private lateinit var newPhone : EditText
    private lateinit var newMin : EditText
    private lateinit var newMax : EditText
    private lateinit var btnUpdate : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        //setting the object for firebase and retrieving the current user's id
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        //initialising
        newEmail = findViewById(R.id.edtEmail)
        newFullname = findViewById(R.id.edtFullname)
        newPhone = findViewById(R.id.edtNumber)
        newMin = findViewById(R.id.edtMin)
        newMax = findViewById(R.id.edtMax)

        uid?.let { uid ->
            // Extracting text from EditText objects
            val email = newEmail.text.toString()
            val fullname = newFullname.text.toString()
            val phone = newPhone.text.toString()
            val minGoals = newMin.text.toString()
            val maxGoals = newMax.text.toString()

            // Update user details with extracted values
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .setPhoneNumber(phone)
                .build()

            auth.currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update successful
                        // You can also update email separately if needed
                        auth.currentUser?.updateEmail(email)
                            ?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    // Email update successful
                                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Email update failed
                                    Toast.makeText(this, "Failed to update email", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Update failed
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}