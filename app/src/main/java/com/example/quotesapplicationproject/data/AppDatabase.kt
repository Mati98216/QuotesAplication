package com.example.quotesapplicationproject.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quotesapplicationproject.data.dao.CategoryDAO
import com.example.quotesapplicationproject.data.dao.QuotesDAO
import com.example.quotesapplicationproject.data.dao.RatingDAO
import com.example.quotesapplicationproject.data.entity.Category
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Quotes::class, Category::class, Rating::class], version = 144)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quotesDAO(): QuotesDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun ratingDAO(): RatingDAO

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }
                    })
                    .allowMainThreadQueries()
                    .build()

                // Call the method to populate tables after the database is created
                GlobalScope.launch(Dispatchers.IO) {
                    populateTables(instance!!)
                }
            }
            return instance!!
        }
        private fun populateTables(database: AppDatabase) {
            val categoryDAO = database.categoryDAO()
            val ratingDAO = database.ratingDAO()

            // Check if the Rating table is already populated
            if (ratingDAO.getCount() == 0) {
                // Prepopulate the Rating table
                val ratings = listOf(
                    Rating(rating = 1.0f),
                    Rating(rating = 1.5f),
                    Rating(rating = 2.0f),
                    Rating(rating = 2.5f),
                    Rating(rating = 3.0f),
                    Rating(rating = 3.5f),
                    Rating(rating = 4.0f),
                    Rating(rating = 4.5f),
                    Rating(rating = 5.0f)
                )
                ratingDAO.insertAll(ratings)

                // Prepopulate the Category table
                val categories = listOf(
                    Category(category = "Życie"),
                    Category(category = "Sport"),
                    Category(category = "Rozrywka"),
                    Category(category = "Muzyka")
                )
                categoryDAO.insertAll(categories)
            }
        }
    }
}