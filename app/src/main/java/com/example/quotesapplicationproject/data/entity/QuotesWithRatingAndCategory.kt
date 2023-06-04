package com.example.quotesapplicationproject.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class QuotesWithRatingAndCategory(
    @Embedded val quotes: Quotes,
    @ColumnInfo(name = "Value") val ratingValue: Float?,
    @ColumnInfo(name = "Category") val category: String?
)