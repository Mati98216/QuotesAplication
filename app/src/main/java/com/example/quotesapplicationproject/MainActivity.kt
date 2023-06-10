package com.example.quotesapplicationproject



import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quotesapplicationproject.data.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerViewHandler: MainActivityRecyclerViewHandler
    private var isMainFragmentVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)
        bottomNavigationView = findViewById(R.id.bottom_navigation)



        recyclerViewHandler = MainActivityRecyclerViewHandler(
            this,
            recyclerView,
            fab,
            AppDatabase.getInstance(applicationContext)
        )

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    isMainFragmentVisible = true
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_item_2 -> {
                    navigateToFragment(AllQuoteFragment())
                    true
                }
                R.id.menu_item_3 -> {
                    navigateToFragment(LikedQuoteFragment())
                    true
                }
                else -> false
            }
        }
    }
    // Otwieranie nowego fragmentu
    private fun navigateToFragment(fragment: Fragment) {
        isMainFragmentVisible = false
        recyclerView.visibility = View.GONE
        fab.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, fragment)
            .addToBackStack(null)
            .commit()
    }
}