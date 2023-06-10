package com.example.quotesapplicationproject.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.R
import com.example.quotesapplicationproject.data.dao.QuotesDAO
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory

class QuoteAdapter(var list: List<QuotesWithRatingAndCategory>,private val quotesDAO: QuotesDAO) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    private lateinit var dialog: Dialog
    //Ustawia obiekt dialogowy dla adaptera.
    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
    }
    //Tworzy nowy ViewHolder na podstawie określonego layoutu.
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var quotes: TextView
        var author: TextView
        var likedQuoteButton: ImageButton
        var ratingValue: TextView
        var category: TextView

        init {
            quotes = view.findViewById(R.id.quote)
            author = view.findViewById(R.id.author)
            likedQuoteButton = view.findViewById(R.id.likedQuote) // Updated to Button
            ratingValue = view.findViewById(R.id.rating_spinner)
            category = view.findViewById(R.id.category_spinner)
            view.setOnClickListener {
                dialog.onClick(layoutPosition)
            }

            likedQuoteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val quote = list[position].quotes
                    // Update only the likedQuote column
                    quote.likedQuote = !quote.likedQuote
                    updateLikedQuote(quote)
                }
            }
        }
    }
    //Tworzy nowy obiekt ViewHolder, który jest odpowiedzialny za zarządzanie widokiem reprezentującym pojedynczy element listy.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_quote, parent, false)
        return ViewHolder(view)
    }
    // Zwraca liczbę elementów w liście
    override fun getItemCount(): Int {
        return list.size
    }
    //Przypisuje dane do konkretnego ViewHoldera na podstawie jego pozycji.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quote = list[position].quotes
        holder.quotes.text = "Quote: ${quote.quote}"
        holder.author.text = "Author: ${quote.author}"
        holder.category.text = "Category: ${list[position].category}"
        holder.ratingValue.text = "Rating: ${list[position].ratingValue?.toString() ?: ""}"


        val likedQuoteImage = if (quote.likedQuote) {
            R.drawable.baseline_thumb_up_24
        } else {
            R.drawable.baseline_thumb_down_24
        }
        holder.likedQuoteButton.setImageResource(likedQuoteImage)
    }
    //Aktualizuje pole LikedQuote w bazie danych
    private fun updateLikedQuote(quote: Quotes) {

        quotesDAO.update(quote)
        val position = list.indexOfFirst { it.quotes.id == quote.id }
        if (position != -1) {
            list[position].quotes = quote
            notifyItemChanged(position)
        }
    }
}