package com.example.focusclock

data class Task (
    val firebaseUUID: String?,
    val tname: String?,
    val tdescription: String?,
    val selectedproject : Project? // references the Project.kt
)