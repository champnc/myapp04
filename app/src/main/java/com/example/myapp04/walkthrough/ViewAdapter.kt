package com.example.myapp04.walkthrough

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp04.R


class ViewAdapter(private val walkthroughList: List<ViewItem>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.walkthrough_template, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return walkthroughList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, currentPage: Int) {
        val viewItem = walkthroughList[currentPage]
        viewHolder.bind(viewItem)
    }
}