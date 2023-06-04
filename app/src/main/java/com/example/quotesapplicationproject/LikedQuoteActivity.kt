package com.example.quotesapplicationproject

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.quotesapplicationproject.adapter.QuoteAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory
import kotlinx.coroutines.*

class LikedQuoteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase
    private lateinit var btnBack: Button
    private var list=mutableListOf<QuotesWithRatingAndCategory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likedquote)
        btnBack = findViewById(R.id.btn_back)
        recyclerView = findViewById(R.id.likedquoteview)
        database = AppDatabase.getInstance(applicationContext)
        adapter = QuoteAdapter(list)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, RecyclerView.VERTICAL))

        getData()

        btnBack.setOnClickListener {
            onBackPressed()
        }
        adapter.setDialog(object : QuoteAdapter.Dialog {
            override fun onClick(position: Int) {
                if (position >= 0 && position < list.size) {

                } else {
                    Log.e("LikedQuoteActivity", "Invalid quote position: $position")
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            list.addAll(database.quotesDAO().getAllForLikedQuotes())
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}