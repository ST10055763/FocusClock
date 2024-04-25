package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // declare form components
    private lateinit var  forgotPasswordLabel: TextView
    private lateinit var signUpLabel: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    // main method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // assign form components to backend
        forgotPasswordLabel = findViewById(R.id.tvLogForgotPass)
        signUpLabel = findViewById(R.id.tvLogSignUp)
        etEmail = findViewById(R.id.edtLogEmail)
        etPass = findViewById(R.id.edtLogPass)
        btnLogin = findViewById(R.id.btnLogLogin)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            // call login method
            login()
        }

        // set listener for Forgot Password
        forgotPasswordLabel.setOnClickListener{
            var forgotPassIntent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(forgotPassIntent)
            // using finish() to end the activity
            finish()
        }

        // set listener for Sign Up
        signUpLabel.setOnClickListener{
            var signUpIntent = Intent(this, SignInActivity::class.java)
            startActivity(signUpIntent)
            // using finish() to end the activity
            finish()
        }
    }

    private fun login() {

        // retrieve login details
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()

                var firebaseUUID = auth.currentUser?.uid // Get the user ID
                val homeScreenIntent = Intent(this, HomePageActivity::class.java)
                homeScreenIntent.putExtra("firebaseUUID", firebaseUUID)

                startActivity(homeScreenIntent)
                finish()
                // add redirect to home screen here
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }
}