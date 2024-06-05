package com.example.focusclock

data class DailyEntry(
    val currentDate: String = "",
    val firebaseUUID: String = "",
    val mingoal: String = "0",
    val maxgoal: String = "0",
    val totalHours: String = "0.0"
)
