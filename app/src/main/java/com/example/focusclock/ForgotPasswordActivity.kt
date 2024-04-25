package com.example.focusclock

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        //need to connect firebase, got scared
        //got passed authentication...did not finish
        auth = FirebaseAuth.getInstance()

        //ask kiash about form.component reference
        val emailEditText: EditText = findViewById(R.id.editTxtEmail)
        val resetButton: Button = findViewById(R.id.btnResetPassword)

        resetButton.setOnClickListener {
            val email = emailEditText.Text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            resetPassword(email)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun resetPassword(email: String) { //add task
        auth.sendPasswordResetEmail(email).addOnCompleteListerner { ->
            if (task.isSuccessful) {
                showToast("Password reset email sent successfully.")
                //Log.d(TAG, "Email sent.") //changing to toast its easier
            }
            else {
                showToast("Failed to send password reset email. Please try again later.")
                //Log.e(TAG, "Failed to send reset email.", task.exception )
            }
        }
    }
    //companion object {
       // private const val TAG = "ForgotPasswordActivity"
    //}
}