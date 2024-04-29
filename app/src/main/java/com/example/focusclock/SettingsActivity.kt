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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        //setting the object for firebase and retrieving the current user's id
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        //initialising
        newEmail = findViewById(R.id.edtEmail)
        newFullname = findViewById(R.id.edtFullname)
        newPhone = findViewById(R.id.edtNumber)
        newMin = findViewById(R.id.edtMin)
        newMax = findViewById(R.id.edtMax)


        uid?.let { uid ->
            val userRef = db.collection("users").document(uid)

            val updates = hashMapOf<String, Any>(
                "email" to newEmail,
                "fname" to newFullname,
                "maxgoals" to newMax,
                "mingoals" to newMin,
                "phoneNum" to newPhone
            )

            userRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "User details updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update user details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}