package com.example.quotesapplicationproject

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.adapter.QuoteAdapter
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.dao.QuotesDAO
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikedQuoteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase
    private lateinit var quotesDAO: QuotesDAO
    private var list = mutableListOf<QuotesWithRatingAndCategory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_likedquote, container, false)

        recyclerView = view.findViewById(R.id.likedquoteview)
        database = AppDatabase.getInstance(requireContext())
        quotesDAO = database.quotesDAO()
        adapter = QuoteAdapter(list, quotesDAO)

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
                            val quoteWithRatingAndCategory = list[position]
                            val quote = quoteWithRatingAndCategory.quotes
                            showFullScreenDialog(quote)
                        }
                        else -> dialogInterface.dismiss()
                    }
                }
                // Display dialog
                val dialogInstance = dialog.show()
                val dialogWindow = dialogInstance.window
                dialogWindow?.setBackgroundDrawableResource(R.color.controls)
                /* dialog.show()*/
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        return view
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun showFullScreenDialog(quote: Quotes) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_full_screen)

        val authorTextView = dialog.findViewById<TextView>(R.id.authorTextView)
        val quoteTextView = dialog.findViewById<TextView>(R.id.quoteTextView)

        authorTextView.text = quote.author
        quoteTextView.text = quote.quote

        quoteTextView.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("quote", quote.quote)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Tekst został skopiowany do schowka", Toast.LENGTH_SHORT).show()
        }

        dialog.window?.decorView?.setOnTouchListener { _, event ->
            val x = event.x.toInt()
            val y = event.y.toInt()

            if (x < quoteTextView.left || x > quoteTextView.right || y < quoteTextView.top || y > quoteTextView.bottom) {
                dialog.dismiss()
            }

            true
        }

        dialog.show()

        // Adjust dialog width and height to match the screen
        dialog.window?.apply {
            attributes = attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }
    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun getData() {
        list.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val likedQuotes = database.quotesDAO().getAllForLikedQuotes()
            withContext(Dispatchers.Main) {
                list.addAll(likedQuotes)
                adapter.notifyDataSetChanged()
            }
        }
    }
}