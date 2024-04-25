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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // missing control elements?
    private lateinit var ReturnLogin: TextView
    private lateinit var btnReturn : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        //need to connect firebase, got scared
        //got passed authentication...did not finish
        auth = FirebaseAuth.getInstance()

        //ask kiash about form.component reference
        val emailEditText: EditText = findViewById(R.id.editTxtEmail)
        val resetButton: Button = findViewById(R.id.btnResetPassword)

        // initialise control elements
        ReturnLogin = findViewById(R.id.tvForgotPassRetLog)
        btnReturn = findViewById(R.id.btnForgotPassBack)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            resetPassword(email)
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

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Failed to send password reset email. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}