package com.example.quotesapplicationproject

import android.annotation.SuppressLint
    import android.content.DialogInterface
    import android.content.Intent
    import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
    import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.quotesapplicationproject.adapter.QuoteAdapter
    import com.example.quotesapplicationproject.data.AppDatabase
    import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory

    import kotlinx.coroutines.*
class QuoteWithCategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private var list = mutableListOf<QuotesWithRatingAndCategory>()
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase
    private var categoryId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_quotewithcategoryactivity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)

        categoryId = arguments?.getInt("category_id", -1) ?: -1
        database = AppDatabase.getInstance(requireContext())
        adapter = QuoteAdapter(list)
        adapter.setDialog(object : QuoteAdapter.Dialog {
            override fun onClick(position: Int) {
                // Creating dialog view
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(list[position].quotes.quote)
                dialog.setItems(R.array.items_option,
                    DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            // Edit
                            val fragment = EditFragment()
                            val bundle = Bundle()
                            bundle.putInt("id", list[position].quotes.id ?:0)
                            fragment.arguments = bundle

                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentManager.beginTransaction()
                                .replace(R.id.fl_wrapper, fragment)
                                .addToBackStack(null)
                                .commit()
                        } else if (which == 1) {
                            // Delete
                            database.quotesDAO().delete(list[position].quotes)
                            getData()
                        } else if (which == 2) {
                            // FullScreen
                            showFullScreenDialog(position)
                        } else {
                            // Cancel
                            dialog.dismiss()
                        }
                    })
                // Display dialog
                val dialogView = dialog.create()
                dialogView.show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        list.clear()
        CoroutineScope(Dispatchers.IO).launch {
            if (categoryId != -1) {
                list.addAll(database.quotesDAO().getAllForCategory(categoryId))
            }
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
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
}
