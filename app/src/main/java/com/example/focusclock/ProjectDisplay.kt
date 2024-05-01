package com.example.focusclock

data class ProjectDisplay (
    val projectID: String?,
    val firebaseUUID: String?,
    val pname: String,
    val ddate: String,
    val ghrs: Int,
    // adding extra optional variables form caluclations = K
    var totTasks: Int?,
    var hoursDone: Double?
)