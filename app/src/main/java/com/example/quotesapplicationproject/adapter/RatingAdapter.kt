package com.example.quotesapplicationproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.quotesapplicationproject.data.entity.Rating

class RatingAdapter(context: Context, ratingList: List<Rating>) :
    ArrayAdapter<Rating>(context, android.R.layout.simple_spinner_item, ratingList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val rating = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = rating?.rating.toString()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val rating = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = rating?.rating.toString()
        return view
    }
}