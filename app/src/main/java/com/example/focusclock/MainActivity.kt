package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var  loginButton: Button
    private lateinit var  signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginButton = findViewById(R.id.btnMainLog)
        signUpButton = findViewById(R.id.btnMainSignUp)

        loginButton.setOnClickListener{
            var loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        signUpButton.setOnClickListener{
            var signUpIntent = Intent(this, SignInActivity::class.java)
            startActivity(signUpIntent)
        }
    }
}