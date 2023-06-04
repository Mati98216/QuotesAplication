package com.example.quotesapplicationproject.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "Category") var category: String?
)