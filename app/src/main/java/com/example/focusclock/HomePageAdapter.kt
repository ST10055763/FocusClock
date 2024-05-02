package com.example.focusclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HomePageAdapter(private val timeentries: List<TimeEntryHomeDisplay>) : RecyclerView.Adapter<HomePageViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(timeEntry: TimeEntryHomeDisplay)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item_layout, parent, false)
        return HomePageViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePageViewHolder, position: Int) {
        val item = timeentries[position]
        holder.bind(item)

        //added click listener
        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = timeentries.size

}