package com.example.quotesapplicationproject

import android.content.Context

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.adapter.CategoryMainAdapter
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityRecyclerViewHandler(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val fab: FloatingActionButton,
    private val database: AppDatabase
) {
    private val list = mutableListOf<Category>()
    private lateinit var adapter: CategoryMainAdapter
    private lateinit var container: View

    init {
        setupRecyclerView()
        setupFab()
        getData()
    }

    private fun setupRecyclerView() {
        val quotesDao = database.quotesDAO()
        adapter = CategoryMainAdapter(list, quotesDao)
        adapter.setDialog(object : CategoryMainAdapter.Dialog {
            override fun onClick(position: Int) {
                val category = list[position]
                navigateToQuoteWithCategoryFragment(category.id)
            }
            override fun onDeleteCategory(position: Int) {

                val category = list[position]
                val categoryId = category.id

                // Usuń kategorię z bazy danych na podstawie categoryId
                CoroutineScope(Dispatchers.IO).launch {
                    // Usunięcie kategorii z bazy danych
                    categoryId?.let { database.categoryDAO().deleteCategoryById(it) }

                    // Odświeżenie danych w widoku
                    withContext(Dispatchers.Main) {
                        list.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }

            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

        recyclerView.visibility = View.VISIBLE
        container = (context as AppCompatActivity).findViewById(R.id.fl_wrapper)
    }

    private fun setupFab() {
        fab.setOnClickListener {
            val fragment = CategoryFragment()
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, fragment)
                .addToBackStack(null)
                .commit()
            fab.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }

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

    private fun navigateToQuoteWithCategoryFragment(categoryId: Int?) {
        if (categoryId != null) {
            val fragment = QuoteWithCategoryFragment()
            val bundle = Bundle()
            bundle.putInt("category_id", categoryId)
            fragment.arguments = bundle

            fab.visibility = View.GONE // Show the container before navigation
            recyclerView.visibility = View.GONE
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            // Handle the case when categoryId is null
        }
    }

}