package com.example.quotesapplicationproject.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.R
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory

class QuoteAdapter(var list: List<QuotesWithRatingAndCategory>) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    private lateinit var dialog: Dialog

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var quotes: TextView
        var author: TextView
        var likedQuote: TextView
        var ratingValue: TextView
        var category: TextView

        init {
            quotes = view.findViewById(R.id.quote)
            author = view.findViewById(R.id.author)
            likedQuote = view.findViewById(R.id.likedQuote)
            ratingValue = view.findViewById(R.id.rating_spinner)
            category = view.findViewById(R.id.category_spinner)
            view.setOnClickListener {
                dialog.onClick(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_quote, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quote = list[position].quotes
        holder.quotes.text = "Quote: ${quote.quote}"
        holder.author.text = "Author: ${quote.author}"
        holder.category.text = "Category: ${list[position].category}"
        holder.ratingValue.text = "Rating: ${list[position].ratingValue?.toString() ?: ""}"
    }

}