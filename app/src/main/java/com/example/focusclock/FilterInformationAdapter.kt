package com.example.focusclock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FilterInformationAdapter(private val timeentries: List<TimeEntryFilterDisplay>) : RecyclerView.Adapter<FilterInformationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterInformationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item_layout, parent, false)
        return FilterInformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterInformationViewHolder, position: Int) {
        val item = timeentries[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = timeentries.size
}