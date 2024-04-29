package com.example.focusclock

import android.widget.Button

data class TimeEntry (
    val firebaseUUID: String,
    val startTime : String,
    val endTime : String,
    val selectedTask: Task,
    val entryProject : Project,
    val timeEntryPicRef:String,
)