package com.example.focusclock

// updated user model
data class Users (
    val firebaseUUID: String,
    val fname: String,
    val email: String,
    val phoneNum : String,
    val mingoals : String,
    val maxgoals : String
)