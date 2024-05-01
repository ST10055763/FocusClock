package com.example.focusclock

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterInformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val projName: TextView = itemView.findViewById(R.id.tvFIProjName)
    private val taskName: TextView = itemView.findViewById(R.id.tvFITaskName)
    private val popDate: TextView = itemView.findViewById(R.id.tvFIDate)
    private val popDuration: TextView = itemView.findViewById(R.id.tvFIDuration)

    fun bind(timeentry: TimeEntryFilterDisplay){
        // Bind project data to views
        projName.text = timeentry.entryProject
        taskName.text = timeentry.selectedTask
        popDate.text = "Date Entry: ${timeentry.dateentry}"
        popDuration.text = "Duration (hh:mm): ${timeentry.durationTask}"
    }
}