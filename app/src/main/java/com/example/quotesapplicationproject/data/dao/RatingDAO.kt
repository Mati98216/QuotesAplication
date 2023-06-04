package com.example.quotesapplicationproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quotesapplicationproject.data.entity.Category
import com.example.quotesapplicationproject.data.entity.Rating
@Dao
interface RatingDAO {
    @Query("SELECT * FROM Rating")
    fun getAll(): LiveData<List<Rating>>
    @Query("SELECT COUNT(*) FROM rating")
    fun getCount(): Int

    @Insert
    fun insert(rating: Rating)
    @Insert
    fun insertAll(rating: List<Rating>)
}
