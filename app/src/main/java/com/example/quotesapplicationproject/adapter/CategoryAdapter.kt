package com.example.quotesapplicationproject.adapter


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.quotesapplicationproject.data.entity.Category

class CategoryAdapter(context: Context, categoryList: List<Category>) :
    ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, categoryList) {
    private lateinit var dialog: Dialog
    fun setDialog(dialog: Dialog){
        this.dialog= dialog
    }
    interface Dialog{
        fun onClick(position: Int)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val category = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = category?.category
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val category = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = category?.category
        return view
    }

}