
package com.example.newsapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapplication.data.local.dao.LikedNewsDao
import com.example.newsapplication.data.local.entity.LikedNewsEntity

@Database(
    entities = [LikedNewsEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun likedNewsDao(): LikedNewsDao
}
