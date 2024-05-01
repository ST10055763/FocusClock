package com.example.focusclock

import android.view.View
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class HomePageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val projName: TextView = itemView.findViewById(R.id.tvHIProjName)
    private val taskName: TextView = itemView.findViewById(R.id.tvHITaskName)
    private val popTimeTracked: TextView = itemView.findViewById(R.id.tvHITimeTracked)
    private val popDuration: TextView = itemView.findViewById(R.id.tvHIDuration)

    fun bind(timeentry: TimeEntryHomeDisplay){
        // Bind project data to views
        projName.text = timeentry.entryProject
        taskName.text = timeentry.selectedTask
        popTimeTracked.text = "Time Done: ${timeentry.startTime} - ${timeentry.endTime}" // Assuming you want to display projectID here
        popDuration.text = "Duration (hh:mm): ${timeentry.durationTask}"
    }
}