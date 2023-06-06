package com.example.quotesapplicationproject

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.quotesapplicationproject.adapter.QuoteAdapter
    import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.coroutines.*
class QuoteWithCategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
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
        fab = view.findViewById(R.id.fab)
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
                            val quoteWithRatingAndCategory = list[position]
                            val quote = quoteWithRatingAndCategory.quotes
                            showFullScreenDialog(quote)

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
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext() , RecyclerView.VERTICAL))
        fab.setOnClickListener {
            val editFragment = EditFragment()
            openFragment(editFragment)
        }


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
    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, fragment)
            .addToBackStack(null)
            .commit()
    }

}
