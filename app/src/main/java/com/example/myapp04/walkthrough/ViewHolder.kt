package com.example.myapp04.walkthrough

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.myapp04.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textTitle = view.findViewById<TextView>(R.id.walkthrough_text)
    private val imageIcon = view.findViewById<LottieAnimationView>(R.id.walkthrough_icon)

    fun bind(viewItem: ViewItem) {
        textTitle.text = viewItem.title
        imageIcon.setAnimation(viewItem.iconID)
    }
}