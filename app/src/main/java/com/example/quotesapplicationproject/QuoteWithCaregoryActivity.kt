package com.example.quotesapplicationproject

import android.annotation.SuppressLint
    import android.content.DialogInterface
    import android.content.Intent
    import android.os.Bundle
    import android.view.WindowManager
    import android.widget.Button
    import android.widget.TextView
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.quotesapplicationproject.adapter.QuoteAdapter
    import com.example.quotesapplicationproject.data.AppDatabase
    import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory

    import kotlinx.coroutines.*
class QuoteWithCaregoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button
    private var list = mutableListOf<QuotesWithRatingAndCategory>()
    private lateinit var adapter: QuoteAdapter
    private lateinit var database: AppDatabase
    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotewithcategoryactivity)

        recyclerView = findViewById(R.id.recycler_view)

        btnBack = findViewById(R.id.btn_back)

        categoryId = intent.getIntExtra("category_id", -1)
        database = AppDatabase.getInstance(applicationContext)
        adapter = QuoteAdapter(list)
        adapter.setDialog(object : QuoteAdapter.Dialog {
            override fun onClick(position: Int) {
                // Creating dialog view
                val dialog = AlertDialog.Builder(this@QuoteWithCaregoryActivity)
                dialog.setTitle(list[position].quotes.quote)
                dialog.setItems(R.array.items_option,
                    DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            // Edit
                            val intent = Intent(this@QuoteWithCaregoryActivity, EditActivity::class.java)
                            intent.putExtra("id", list[position].quotes.id)
                            startActivity(intent)
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
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, RecyclerView.VERTICAL))



        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("InflateParams")
    private fun showFullScreenDialog(position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
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
            if (categoryId != -1) {
                list.addAll(database.quotesDAO().getAllForCategory(categoryId))
            }
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}