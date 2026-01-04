
package com.example.newsapplication.data.local.dao

import androidx.room.*
import com.example.newsapplication.data.local.entity.LikedNewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun likeNews(news: LikedNewsEntity)

    @Delete
    suspend fun unlikeNews(news: LikedNewsEntity)

    @Query("DELETE FROM liked_news WHERE newsId = :newsId")
    suspend fun unlikeNewsById(newsId: Int)

    @Query("SELECT * FROM liked_news ORDER BY likedAt DESC")
    fun getLikedNews(): Flow<List<LikedNewsEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_news WHERE newsId = :newsId)")
    fun isNewsLiked(newsId: Int): Flow<Boolean>
}
