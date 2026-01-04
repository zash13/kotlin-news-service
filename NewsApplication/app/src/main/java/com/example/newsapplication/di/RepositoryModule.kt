
package com.example.newsapplication.di

import com.example.newsapplication.data.local.dao.LikedNewsDao
import com.example.newsapplication.data.repository.INewsRepository
import com.example.newsapplication.data.repository.NewsRepository
import com.example.newsapplication.network.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNewsRepository(
        api: NewsApiService,
        likedNewsDao: LikedNewsDao
    ): INewsRepository = NewsRepository(api, likedNewsDao)
}
