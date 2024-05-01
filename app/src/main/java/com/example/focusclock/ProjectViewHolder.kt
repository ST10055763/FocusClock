package com.example.focusclock

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val projName: TextView = itemView.findViewById(R.id.tvPIPName)
    private val dueDate: TextView = itemView.findViewById(R.id.tvPIDDate)
    private val popTotTasks: TextView = itemView.findViewById(R.id.tvPITotalPop)
    private val popHoursDone: TextView = itemView.findViewById(R.id.tvPIHoursPop)
    private val popGoalHours: TextView = itemView.findViewById(R.id.tvPIGoalPop)

    fun bind(project: ProjectDisplay){
        // Bind project data to views
        projName.text = project.pname
        dueDate.text = "Due Date:  ${project.ddate}"
        popTotTasks.text = "Total Tasks: ${project.totTasks}" // Assuming you want to display projectID here
        popHoursDone.text = "Hours Done: ${project.hoursDone}"
        popGoalHours.text = "Goal Hours: ${project.ghrs}"
    }
}