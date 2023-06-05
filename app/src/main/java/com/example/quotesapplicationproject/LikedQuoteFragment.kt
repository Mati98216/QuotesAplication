package com.example.quotesapplicationproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.adapter.QuoteAdapter
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikedQuoteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase

    private var list = mutableListOf<QuotesWithRatingAndCategory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_likedquote, container, false)

        recyclerView = view.findViewById(R.id.likedquoteview)
        database = AppDatabase.getInstance(requireContext())
        adapter = QuoteAdapter(list)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))


        adapter.setDialog(object : QuoteAdapter.Dialog {
            override fun onClick(position: Int) {
                if (position >= 0 && position < list.size) {

                } else {
                    Log.e("LikedQuoteFragment", "Invalid quote position: $position")
                }
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val likedQuotes = database.quotesDAO().getAllForLikedQuotes()
            withContext(Dispatchers.Main) {
                list.addAll(likedQuotes)
                adapter.notifyDataSetChanged()
            }
        }
    }
}