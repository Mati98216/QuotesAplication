package com.example.quotesapplicationproject
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quotesapplicationproject.data.AppDatabase
import com.example.quotesapplicationproject.data.entity.Category

class CategoryFragment : Fragment() {
    private lateinit var categoryName: EditText
    private lateinit var btnSaveCategory: Button
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_category, container, false)

        categoryName = view.findViewById(R.id.category)
        btnSaveCategory = view.findViewById(R.id.btn_save)
        database = AppDatabase.getInstance(requireContext())

        btnSaveCategory.setOnClickListener {
            val category = categoryName.text.toString()

            if (category.isNotEmpty()) {
                // Sprawdzenie, czy kategoria już istnieje w bazie danych
                val existingCategory = database.categoryDAO().getCategoryByName(category)

                if (existingCategory != null) {
                    Toast.makeText(requireContext(), "Kategoria $category już istnieje", Toast.LENGTH_SHORT).show()
                } else {
                    val categoryList = listOf(Category(null, category))
                    // Insert category into the database
                    database.categoryDAO().insertAll(categoryList)

                    requireActivity().finish()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            } else {
                Toast.makeText(requireContext(), "Wprowadź nazwę kategorii", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}