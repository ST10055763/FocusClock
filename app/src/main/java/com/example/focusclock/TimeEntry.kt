package com.example.focusclock

import android.widget.Button

data class TimeEntry (
    val startTime : Int,
    val endTime : Int,
    val selectedTask: Task,
    val entryProject : Project,
    val timeEntryPic:Button
)