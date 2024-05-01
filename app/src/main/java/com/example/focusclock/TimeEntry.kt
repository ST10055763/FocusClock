package com.example.focusclock

import android.widget.Button
import java.util.Date

data class TimeEntry (
    val currentDate: String?,
    val firebaseUUID: String?,
    val startTime : String?,
    val endTime : String?,
    val selectedTask: String?,
    val entryProject : String?,
    val timeEntryPicRef:String?,
)