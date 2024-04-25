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

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
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

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            resetPassword(email)
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