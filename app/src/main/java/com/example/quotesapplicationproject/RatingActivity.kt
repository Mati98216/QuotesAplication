package com.example.quotesapplicationproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Rating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class RatingActivity : AppCompatActivity() {
    private lateinit var ratingValue: EditText
    private lateinit var btnSaveRating: Button
    private lateinit var database: AppDatabase
    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        ratingValue = findViewById(R.id.rating)
        btnSaveRating = findViewById(R.id.btn_save)
        database = AppDatabase.getInstance(applicationContext)

        btnSaveRating.setOnClickListener {
            val rating = ratingValue.text.toString().toFloatOrNull()

            if (rating != null) {
                val ratingList = listOf(Rating(null, rating))
                // Insert rating into the database
                database.ratingDAO().insertAll(ratingList)
                startActivity(Intent(this@RatingActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, "Invalid rating value", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}