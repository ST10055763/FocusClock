package com.example.focusclock

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
//import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {
    lateinit var SignUpEmail: EditText
    lateinit var SignUpPassword: EditText
    lateinit var signUpBtn: Button
    lateinit var SignUpFullname : EditText
    lateinit var SignUpPhoneNumber : EditText
    lateinit var SignUpConfirmPass : EditText
    lateinit var SignUpMaxGoals : EditText
    lateinit var SignUpMinGoals : EditText

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        SignUpEmail = findViewById(R.id.SUEmailTxt)
        SignUpPassword = findViewById(R.id.SUPasswordTxt)
        signUpBtn = findViewById(R.id.SignUpBtn)

        auth = Firebase.auth

        signUpBtn.setOnClickListener {
            signupUser()
        }
       // val db = FirebaseFirestore.getInstance()
        //val usersCollection = db.collection("users")
        //val currentUser = auth.currentUser
        //val userRef = db.collection("users").document(currentUser.uid)


        val fname = SignUpFullname.text.toString()
        val phoneNum = SignUpPhoneNumber.text.toString()
        val mingoals = SignUpMinGoals.text.toString()
        val maxgoals = SignUpMaxGoals.text.toString()

        val newUser = Users(
            fname = fname,
            phoneNum = phoneNum,
            mingoals = mingoals,
            maxgoals = maxgoals
        )
       // userRef.set(newUser).addOnSuccessListener {}
    }

    fun signupUser() {
        val SUemail = SignUpEmail.toString()
        val SUpass = SignUpPassword.toString()

        if (SUemail.isBlank() || SUpass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(SUemail, SUpass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
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
}