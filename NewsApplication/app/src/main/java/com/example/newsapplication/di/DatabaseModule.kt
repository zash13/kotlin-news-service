
package com.example.newsapplication.di

import android.content.Context
import androidx.room.Room
import com.example.newsapplication.data.local.NewsDatabase
import com.example.newsapplication.data.local.dao.LikedNewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLikedNewsDao(database: NewsDatabase): LikedNewsDao {
        return database.likedNewsDao()
    }
}
