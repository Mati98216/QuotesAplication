package com.example.quotesapplicationproject.data.dao

import androidx.room.*
import com.example.quotesapplicationproject.data.entity.Quotes
import com.example.quotesapplicationproject.data.entity.QuotesWithRatingAndCategory

@Dao
interface QuotesDAO {
    @Query("SELECT * FROM quotes WHERE Quote = :quote AND Author = :author")
    fun getQuoteByQuoteAndAuthor(quote: String, author: String): Quotes?
    @Query("SELECT quotes.*, rating.Value, category.Category FROM quotes INNER JOIN rating ON quotes.ratingId = rating.id INNER JOIN category ON quotes.categoryId = category.id")
    fun getAll(): List<QuotesWithRatingAndCategory>
    @Query("SELECT quotes.*, rating.Value, category.Category FROM quotes INNER JOIN rating ON quotes.ratingId = rating.id INNER JOIN category ON quotes.categoryId = category.id WHERE quotes.categoryId = :categoryId")
    fun getAllForCategory(categoryId: Int): List<QuotesWithRatingAndCategory>
    @Query("SELECT quotes.*, rating.Value, category.Category FROM quotes INNER JOIN rating ON quotes.ratingId = rating.id INNER JOIN category ON quotes.categoryId = category.id WHERE quotes.likedQuote = 1")
    fun getAllForLikedQuotes():List<QuotesWithRatingAndCategory>
    @Query("SELECT * FROM quotes WHERE id IN (:quotesIds)")
    fun loadAllByIds(quotesIds: IntArray): List<Quotes>
    
    @Insert
    fun insertAll(vararg quotes: Quotes)

    @Delete
    fun delete(quotes: Quotes)

    @Query("Select * FROM quotes WHERE id=:id")
    fun get(id:Int): Quotes
    @Update
    fun update(quotes: Quotes)
}