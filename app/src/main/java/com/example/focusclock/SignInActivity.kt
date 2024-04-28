package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    lateinit var SignUpEmail: EditText
    lateinit var SignUpPassword: EditText
    lateinit var signUpBtn: Button
    lateinit var SignUpFullname : EditText
    lateinit var SignUpPhoneNumber : EditText
    lateinit var SignUpConfirmPass : EditText
    lateinit var SignUpMaxGoals : EditText
    lateinit var SignUpMinGoals : EditText

    // missing control elements?
    private lateinit var ReturnLogin: TextView
    private lateinit var btnReturn : FloatingActionButton

    // declare firebase variables needed (db missing and Firestore incorrectly handled)
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // View Bindings (Needed to add all to recognise successfuly)
        SignUpEmail = findViewById(R.id.SUEmailTxt)
        SignUpPassword = findViewById(R.id.SUPasswordTxt)
        signUpBtn = findViewById(R.id.SignUpBtn)
        SignUpFullname = findViewById(R.id.SUFullNameTxt)
        SignUpPhoneNumber = findViewById(R.id.SUPhoneTxt)
        SignUpConfirmPass = findViewById(R.id.SUConfirmPasswordTxt)
        SignUpMaxGoals = findViewById(R.id.SUMaxGoalsTxt)
        SignUpMinGoals = findViewById(R.id.SUMinGoalsTxt)

        // initialise control elements
        ReturnLogin = findViewById(R.id.SUReturnLogin)
        btnReturn = findViewById(R.id.SUFloatButRet)

        // Initialising auth object
        auth = Firebase.auth

        // set signUpButton Listener
        signUpBtn.setOnClickListener {
            signupUser()
        }

        // setup return listeners (did because missing)
        ReturnLogin.setOnClickListener{
            var returnLoginIntent = Intent(this, LoginActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

        btnReturn.setOnClickListener{
            var returnLoginIntent = Intent(this, LoginActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

    }

    fun signupUser() {
        val SUemail = SignUpEmail.text.toString()
        val SUpass = SignUpPassword.text.toString()
        // added confirm password check
        val SUConfirmPass = SignUpConfirmPass.text.toString()

        // added conform pass as a check
        if (SUemail.isBlank() || SUpass.isBlank() || SUConfirmPass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        // added confirm pass check
        if (SUpass != SUConfirmPass) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(SUemail, SUpass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
//                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
//                finish()
                // this code added to write to the Firestore db successfully
                val userId = auth.currentUser?.uid // Get the user ID
                if (userId != null) {
                    createProfile(SUemail, userId) // Pass the user ID to createProfile
                } else {
                    Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                }
                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
        signUpBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createProfile(SUemail: String, userId: String) {

        val fname = SignUpFullname.text.toString()
        val phoneNum = SignUpPhoneNumber.text.toString()
        val mingoals = SignUpMinGoals.text.toString()
        val maxgoals = SignUpMaxGoals.text.toString()

        val newUser = Users(
            firebaseUUID = userId,
            fname = fname,
            email = SUemail,
            phoneNum = phoneNum,
            mingoals = mingoals,
            maxgoals = maxgoals
        )

        if (userId.isNotEmpty() && SUemail.isNotEmpty() && fname.isNotEmpty() && phoneNum.isNotEmpty() && mingoals.isNotEmpty() && maxgoals.isNotEmpty())
        {
            db.collection("profiles")
                .document(userId)
                .set(newUser) // Use userId as document name
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Created Successfully", Toast.LENGTH_SHORT).show()
                    // add homescreen redirect here
                    // val intent = Intent(this, HomeActivity::class.java)
                    // startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to Create Profile", Toast.LENGTH_SHORT).show()
                }
        }

    }

}