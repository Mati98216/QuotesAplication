package com.example.quotesapplicationproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quotesapplicationproject.data.entity.Category
import com.example.quotesapplicationproject.data.entity.Rating

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM Category WHERE Category = :name")
    fun getCategoryByName(name: String): Category?
    @Query("SELECT * FROM Category")
    fun getAll(): LiveData<List<Category>>
    @Query("SELECT * FROM Category")
    fun getAllCategory(): List<Category>

    @Insert
    fun insert(category: Category)
    @Insert
    fun insertAll(category: List<Category>)
}