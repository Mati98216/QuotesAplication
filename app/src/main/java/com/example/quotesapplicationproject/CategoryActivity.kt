package com.example.quotesapplicationproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category

class CategoryActivity : AppCompatActivity() {
    private lateinit var categoryName: EditText
    private lateinit var btnSaveCategory: Button
    private lateinit var database: AppDatabase
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        btnBack = findViewById(R.id.btn_back)
        categoryName = findViewById(R.id.category)
        btnSaveCategory = findViewById(R.id.btn_save)
        database = AppDatabase.getInstance(applicationContext)

        btnSaveCategory.setOnClickListener {
            val category = categoryName.text.toString()

            if (category.isNotEmpty()) {
                // Sprawdzenie, czy kategoria już istnieje w bazie danych
                val existingCategory = database.categoryDAO().getCategoryByName(category)

                if (existingCategory != null) {
                    Toast.makeText(applicationContext, "Kategoria $category już istnieje", Toast.LENGTH_SHORT).show()
                } else {
                    val categoryList = listOf(Category(null, category))
                    // Insert category into the database
                    database.categoryDAO().insertAll(categoryList)

                    finish()
                    startActivity(Intent(this@CategoryActivity, MainActivity::class.java))
                }
            } else {
                Toast.makeText(applicationContext, "Wprowadź nazwę kategorii", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}