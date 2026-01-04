
package com.example.newsapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_news")
data class LikedNewsEntity(
    @PrimaryKey
    val newsId: Int,
    val categoryId: Int,
    val title: String,
    val shortDescription: String,
    val likedAt: Long = System.currentTimeMillis(),
)
