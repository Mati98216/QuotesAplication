package com.example.quotesapplicationproject.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rating::class,
            parentColumns = ["id"],
            childColumns = ["ratingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Quotes(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "Quote") var quote: String?,
    @ColumnInfo(name = "Author") var author: String?,
    @ColumnInfo(name = "likedQuote") var likedQuote: Boolean = false,
    @ColumnInfo(name = "ratingId") var ratingId: Int?,
    @ColumnInfo(name = "categoryId") var categoryId: Int?
)
