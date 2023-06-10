package com.example.quotesapplicationproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.Rating
import kotlinx.coroutines.*

class EditFragment : Fragment() {
    private lateinit var quotes: EditText
    private lateinit var author: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLike: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var ratingSpinner: Spinner
    private var likedQuote: Boolean = false
    private var categoryId: Int = 0
    private var ratingId: Int = 0
    private lateinit var database: AppDatabase
    private  var categoryList: List<Category> = emptyList()
    private  var ratingList: List<Rating> = emptyList()

    // Inicjalizacja CoroutineScope przy pomocy lazy initialization
    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inicjalizacja widoku fragmentu
        val view = inflater.inflate(R.layout.activity_edit, container, false)
        // Inicjalizacja pól
        quotes = view.findViewById(R.id.quote)
        author = view.findViewById(R.id.author)
        btnSave = view.findViewById(R.id.btn_save)
        btnLike = view.findViewById(R.id.btn_like)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        ratingSpinner = view.findViewById(R.id.ratingSpinner)

        database = AppDatabase.getInstance(requireContext())

        fetchCategories()
        fetchRatings()

        val quoteId = arguments?.getInt("id", 0) ?: 0
        val quote = database.quotesDAO().get(quoteId)
        quote?.let { quote ->
            quotes.setText(quote.quote)
            author.setText(quote.author)
            likedQuote = quote.likedQuote
            categoryId = quote.categoryId ?: 0
            ratingId = quote.ratingId ?: 0

            val selectedCategoryPosition = categoryList.indexOfFirst { it.id == categoryId }
            categorySpinner.setSelection(selectedCategoryPosition)

            val selectedRatingPosition = ratingList.indexOfFirst { it.id == ratingId }
            ratingSpinner.setSelection(selectedRatingPosition)
        }
        // Inicjalizacja listenerów dla spinnerów
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoryId = categoryList[position].id ?: 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action performed when nothing is selected
            }
        }

        ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ratingId = ratingList[position].id ?: 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action performed when nothing is selected
            }
        }
        // Ustawienie listenera dla przycisku btnSave
        btnSave.setOnClickListener {
            // Sprawdzenie czy pola tekstowe nie są puste
            if (quotes.text.isNotEmpty() && author.text.isNotEmpty()) {
                val quoteText = quotes.text.toString()
                val authorName = author.text.toString()

                val quote = database.quotesDAO().get(quoteId)
                if (quote != null) {
                    // Edycja istenijącego cytatu
                    if (isQuoteExists(quoteText, authorName, categoryId, ratingId,likedQuote)) {
                        Toast.makeText(requireContext(), "Cytat już istnieje", Toast.LENGTH_SHORT).show()
                    } else {
                        updateQuote(quote, quoteText, authorName)
                    }
                } else {

                    if (isQuoteAndAuthorExist(quoteText, authorName)) {
                        // Jeśli cytatu o podanym tekście i autorze już istnieje w bazie danych, wyświetlenie stosownego komunikatu
                        Toast.makeText(requireContext(), "Cytat i autor już istnieją", Toast.LENGTH_SHORT).show()
                    } else {
                        // Jeśli nie istnieje, dodanie nowego cytatu do bazy danych
                        addNewQuote(quoteText, authorName)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Wprowadź dane w polach tekstowych", Toast.LENGTH_SHORT).show()
            }
        }
        btnLike.setOnClickListener {
            likedQuote = !likedQuote
            if (likedQuote) {
                Toast.makeText(requireContext(), "Polubione", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Niepolubione", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    // Metoda odpowiedzialna za pobranie listy kategorii z bazy danych
    private fun fetchCategories() {
        database.categoryDAO().getAll().observe(viewLifecycleOwner, { categories ->
            categories?.let {
                Log.d("EditFragment", "Fetched category data: $categories")
                categoryList = it
                val categoryNames = categoryList.map { category -> category.category }
                val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = categoryAdapter

                // Restore selected category
                val selectedCategoryPosition = categoryList.indexOfFirst { category -> category.id == categoryId }
                categorySpinner.setSelection(selectedCategoryPosition)
            }
        })
    }
    // Metoda odpowiedzialna za pobranie listy ocen z bazy danych
    private fun fetchRatings() {
        database.ratingDAO().getAll().observe(viewLifecycleOwner, { ratings ->
            ratings?.let {
                Log.d("EditFragment", "Fetched rating data: $ratings")
                ratingList = it
                val ratingValues = ratingList.map { rating -> rating.rating.toString() }
                val ratingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ratingValues)
                ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ratingSpinner.adapter = ratingAdapter

                // Restore selected rating
                val selectedRatingPosition = ratingList.indexOfFirst { rating -> rating.id == ratingId }
                ratingSpinner.setSelection(selectedRatingPosition)
            }
        })
    }
    // Metoda dodająca nowy cytat do bazy danych
    private fun addNewQuote(quoteText: String, authorName: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val newQuote = Quotes(
                    null,
                    quoteText,
                    authorName,
                    likedQuote,
                    ratingId,
                    categoryId
                )
                newQuote.ratingId = ratingId
                newQuote.categoryId = categoryId
                database.quotesDAO().insertAll(newQuote)
            }
            requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
    // Metoda aktualizująca istniejący cytat w bazie danych
    private fun updateQuote(quote: Quotes, quoteText: String, authorName: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                quote.quote = quoteText
                quote.author = authorName
                quote.likedQuote = likedQuote
                quote.ratingId = ratingId
                quote.categoryId = categoryId
                database.quotesDAO().update(quote)
            }
            requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
    // Metoda sprawdzająca czy istnieje cytatu o podanym tekście i autorze w bazie danych
    private fun isQuoteAndAuthorExist(quote: String, author: String): Boolean {
        val existingQuote = database.quotesDAO().getQuoteByQuoteAndAuthor(quote, author)
        return existingQuote != null
    }
    // Metoda sprawdzająca czy istnieje cytatu o podanym tekście, autorze, kategorii, ocenie i polubieniu w bazie danych
    private fun isQuoteExists(quote: String, author: String, categoryId: Int, ratingId: Int,isLikedQuote:Boolean): Boolean {
        val existingQuote = database.quotesDAO().getQuoteByDetails(quote, author, categoryId, ratingId,isLikedQuote)
        return existingQuote != null
    }
}


