package com.example.quotesapplicationproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.Rating
import kotlinx.coroutines.*

class EditActivity : AppCompatActivity() {
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
    private lateinit var categoryList: List<Category>
    private lateinit var ratingList: List<Rating>
    private lateinit var btnBack: Button
    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        btnBack = findViewById(R.id.btn_back)
        quotes = findViewById(R.id.quote)
        author = findViewById(R.id.author)
        btnSave = findViewById(R.id.btn_save)
        btnLike = findViewById(R.id.btn_like)
        categorySpinner = findViewById(R.id.categorySpinner)
        ratingSpinner = findViewById(R.id.ratingSpinner)

        database = AppDatabase.getInstance(applicationContext)

        fetchCategories()
        fetchRatings()

        val intent = intent.extras
        if (intent != null) {
            val id = intent.getInt("id", 0)
            val quote = database.quotesDAO().get(id)

            quotes.setText(quote.quote)
            author.setText(quote.author)
            likedQuote = quote.likedQuote
            categoryId = quote.categoryId ?: 0
            ratingId = quote.ratingId ?: 0

            // Set selected category and rating on spinners
            val selectedCategoryPosition = categoryList.indexOfFirst { it.id == categoryId }
            categorySpinner.setSelection(selectedCategoryPosition)

            val selectedRatingPosition = ratingList.indexOfFirst { it.id == ratingId }
            ratingSpinner.setSelection(selectedRatingPosition)
        }

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

        btnSave.setOnClickListener {
            if (quotes.text.isNotEmpty() && author.text.isNotEmpty()) {
                val quote = quotes.text.toString()
                val authorName = author.text.toString()

                if (isQuoteAndAuthorExist(quote, authorName)) {
                    Toast.makeText(applicationContext, "Cytat i autor już istnieją", Toast.LENGTH_SHORT).show()
                } else {
                    coroutineScope.launch {
                        // Perform database operations on the IO thread
                        withContext(Dispatchers.IO) {
                            if (intent != null) {
                                // Edit data
                                val updatedQuote = Quotes(
                                    intent.getInt("id", 0),
                                    quote,
                                    authorName,
                                    likedQuote,
                                    ratingId,
                                    categoryId
                                )
                                database.quotesDAO().update(updatedQuote)
                            } else {
                                // Insert new data
                                val newQuote = Quotes(
                                    null,
                                    quote,
                                    authorName,
                                    likedQuote,
                                    ratingId,
                                    categoryId
                                )
                                newQuote.ratingId = ratingId
                                newQuote.categoryId = categoryId
                                database.quotesDAO().insertAll(newQuote)
                            }
                        }
                        startActivity(Intent(this@EditActivity, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Wprowadź dane w polach tekstowych", Toast.LENGTH_SHORT).show()
            }
        }

        btnLike.setOnClickListener {
            likedQuote = !likedQuote
            if (likedQuote) {
                Toast.makeText(applicationContext, "Polubione", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Niepolubione", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchCategories() {
        database.categoryDAO().getAll().observe(this, { categories ->
            categories?.let {
                Log.d("EditActivity", "Fetched category data: $categories")
                categoryList = it
                val categoryNames = categoryList.map { category -> category.category }
                val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = categoryAdapter

                // Restore selected category
                val selectedCategoryPosition = categoryList.indexOfFirst { category -> category.id == categoryId }
                categorySpinner.setSelection(selectedCategoryPosition)
            }
        })
    }

    private fun fetchRatings() {
        database.ratingDAO().getAll().observe(this, { ratings ->
            ratings?.let {
                Log.d("EditActivity", "Fetched rating data: $ratings")
                ratingList = it
                val ratingValues = ratingList.map { rating -> rating.rating.toString() }
                val ratingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratingValues)
                ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ratingSpinner.adapter = ratingAdapter

                // Restore selected rating
                val selectedRatingPosition = ratingList.indexOfFirst { rating -> rating.id == ratingId }
                ratingSpinner.setSelection(selectedRatingPosition)
            }
        })
    }

    private fun isQuoteAndAuthorExist(quote: String, author: String): Boolean {
        val existingQuote = database.quotesDAO().getQuoteByQuoteAndAuthor(quote, author)
        return existingQuote != null
    }
}