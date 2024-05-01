package com.example.focusclock

data class TimeEntryFilterDisplay (
    val firebaseUUID: String,
    val startTime : String,
    val endTime : String,
    val selectedTask: String,
    val entryProject : String,
    val timeEntryPicRef: String,

    // NEED TO ADD
    val dateentry: String,
    // extra for display
    var durationTask: Double?
)