package com.example.quotesapplicationproject


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.quotesapplicationproject.adapter.CategoryMainAdapter
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private var list = mutableListOf<Category>()
    private lateinit var adapter: CategoryMainAdapter
    private lateinit var database: AppDatabase
    private lateinit var btncategory: Button
    private lateinit var btnquote: Button
    private lateinit var btnlikedquote: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)
        btnlikedquote=findViewById(R.id.likedQuote)
        btnquote = findViewById(R.id.btnquote)
        btncategory = findViewById(R.id.btncategory)
        database = AppDatabase.getInstance(applicationContext)
        adapter = CategoryMainAdapter(list)
        adapter.setDialog(object : CategoryMainAdapter.Dialog {
            override fun onClick(position: Int) {
                val category = list[position]
                navigateToQuoteMainActivity(category)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            applicationContext,
            VERTICAL,
            false
        )
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                VERTICAL
            )
        )
        btnlikedquote.setOnClickListener {
            startActivity(Intent(this, LikedQuoteActivity::class.java))
        }
        fab.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
        btncategory.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }
        btnquote.setOnClickListener {
            startActivity(Intent(this, AllQuoteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        list.clear()
        CoroutineScope(Dispatchers.IO).launch {
            list.addAll(database.categoryDAO().getAllCategory())
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun navigateToQuoteMainActivity(category: Category) {
        val intent = Intent(this, QuoteWithCaregoryActivity::class.java)
        intent.putExtra("category_id", category.id)
        startActivity(intent)
    }
}