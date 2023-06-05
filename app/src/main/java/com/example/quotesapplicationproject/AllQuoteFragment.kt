package com.example.quotesapplicationproject

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.adapter.QuoteAdapter
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class AllQuoteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private var list = mutableListOf<QuotesWithRatingAndCategory>()
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_allquotemain, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)

        database = AppDatabase.getInstance(requireContext())
        adapter = QuoteAdapter(list)
        adapter.setDialog(object : QuoteAdapter.Dialog {
            override fun onClick(position: Int) {
                // Creating dialog view
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(list[position].quotes.quote)
                dialog.setItems(R.array.items_option) { dialogInterface, which ->
                    when (which) {
                        0 -> {
                            // Edit
                            val editFragment = EditFragment()
                            val args = Bundle()
                            args.putInt("id", list[position].quotes.id ?:0)
                            editFragment.arguments = args
                            openFragment(editFragment)
                        }
                        1 -> {
                            // Delete
                            database.quotesDAO().delete(list[position].quotes)
                            getData()
                        }
                        2 -> {
                            // FullScreen
                            showFullScreenDialog(position)
                        }
                        else -> dialogInterface.dismiss()
                    }
                }
                // Display dialog
                dialog.show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        fab.setOnClickListener {
            val editFragment = EditFragment()
            openFragment(editFragment)
        }

        return view
    }

    @SuppressLint("InflateParams")
    private fun showFullScreenDialog(position: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_full_screen, null)
        val quoteTextView = dialogView.findViewById<TextView>(R.id.quoteTextView)
        val authorTextView = dialogView.findViewById<TextView>(R.id.authorTextView)

        quoteTextView.text = list[position].quotes.quote
        authorTextView.text = list[position].quotes.author

        dialogBuilder.setView(dialogView)
        dialogBuilder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val dialog = dialogBuilder.create()
        dialog.show()

        // Adjust dialog width to match the screen
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        list.clear()
        CoroutineScope(Dispatchers.IO).launch {
            list.addAll(database.quotesDAO().getAll())
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, fragment)
            .addToBackStack(null)
            .commit()
    }
}