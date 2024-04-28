package com.example.focusclock

data class Task (
    val firebaseUUID: String,
    val tname: String,
    val tdescription: String,
    val project : Project // references the Project.kt
)