package com.example.quotesapplicationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.R
import com.example.quotesapplicationproject.data.dao.QuotesDAO
import com.example.quotesapplicationproject.data.entity.Category

class CategoryMainAdapter(private var list: List<Category>, private val quotesDao: QuotesDAO) :
    RecyclerView.Adapter<CategoryMainAdapter.ViewHolder>() {

    private lateinit var dialog: Dialog
    //Ustawia obiekt dialogowy dla adaptera.
    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
        fun onDeleteCategory(position: Int)
    }
    //Tworzy nowy ViewHolder na podstawie określonego layoutu.
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryName: TextView
        var emptyCategoryImage: ImageView

        init {
            categoryName = view.findViewById(R.id.categorypage)
            emptyCategoryImage = view.findViewById(R.id.deletecategory)

            view.setOnClickListener {
                dialog.onClick(layoutPosition)
            }
        }
    }
    //Tworzy nowy obiekt ViewHolder, który jest odpowiedzialny za zarządzanie widokiem reprezentującym pojedynczy element listy.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false)
        return ViewHolder(view)
    }
    // Zwraca liczbę elementów w liście
    override fun getItemCount(): Int {
        return list.size
    }
    //Przypisuje dane do konkretnego ViewHoldera na podstawie jego pozycji.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = list[position]
        holder.categoryName.text = category.category

        val quotesCount = getQuotesCountForCategory(category)

        if (quotesCount != null) {
            if (quotesCount > 0) {
                holder.emptyCategoryImage.visibility = View.GONE
            } else {
                holder.emptyCategoryImage.visibility = View.VISIBLE
                holder.emptyCategoryImage.setOnClickListener {
                    val adapterPosition = holder.adapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        dialog.onDeleteCategory(adapterPosition)
                    }
                }
            }
        }
    }
    //Zwraca liczbę cytatów dla danej kategorii.
    private fun getQuotesCountForCategory(category: Category): Int? {
        val categoryId = category.id


        val quotesCount = categoryId?.let { quotesDao.getAllForCategory(it).size }

        return quotesCount
    }
}