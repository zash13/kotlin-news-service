package com.example.newsapplication.data.repository

import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.dto.FullNewsDto
import com.example.newsapplication.data.dto.NewsTitleDto
import com.example.newsapplication.data.local.dao.LikedNewsDao
import com.example.newsapplication.data.local.entity.LikedNewsEntity
import com.example.newsapplication.data.mapper.toDto
import com.example.newsapplication.data.mapper.toNewsTitleDto
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.data.models.MultiCategoriesRequest
import com.example.newsapplication.data.models.UserCategory
import com.example.newsapplication.network.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface INewsRepository {
    suspend fun checkHealth(): String

    suspend fun getCategories(): List<CategoryDto>

    fun getUserCategories(): StateFlow<List<UserCategory>>

    suspend fun setUserCategories(categoryIds: Set<Int>)

    suspend fun getNewsTitles(categoryIds: Set<Int>): List<NewsTitleDto>

    suspend fun getNewsTitles(): List<NewsTitleDto>

    suspend fun getNewsById(newsId: Int): FullNewsDto?

    suspend fun searchNews(query: String): List<NewsTitleDto>

    /*
    suspend fun uploadImage(
        file: MultipartBody.Part,
        altText: String?,
    ): Int

     */
    suspend fun downloadImage(imageId: Int): okhttp3.ResponseBody?

    suspend fun getImageBitmap(imageId: Int?): android.graphics.Bitmap?

    // local database
    fun getLikedNews(): Flow<List<NewsTitleDto>>

    fun isNewsLiked(newsId: Int): Flow<Boolean>

    suspend fun likeNews(
        newsId: Int,
        categoryId: Int,
        title: String,
        shortDescription: String,
    )

    suspend fun unlikeNews(newsId: Int)
}

class NewsRepository(
    private val apiService: NewsApiService,
    private val likedNewsDao: LikedNewsDao,
) : INewsRepository {
    private val _userCategories = MutableStateFlow<List<UserCategory>>(emptyList())

    override fun getUserCategories(): StateFlow<List<UserCategory>> = _userCategories.asStateFlow()

    override suspend fun setUserCategories(categoryIds: Set<Int>) {
        _userCategories.value =
            categoryIds.map { categoryId ->
                UserCategory(categoryId = categoryId)
            }
    }

    override suspend fun checkHealth(): String = apiService.checkHealth()

    override suspend fun getCategories(): List<CategoryDto> = apiService.getCategories().data.map { it.toDto() }

    override suspend fun getNewsTitles(categoryIds: Set<Int>): List<NewsTitleDto> =
        categoryIds.flatMap { categoryId ->
            apiService.getTitlesByCategory(categoryId, 5).data.map { it.toNewsTitleDto() }
        }

    override suspend fun getNewsTitles(): List<NewsTitleDto> = apiService.getNewestTitles(null).data.map { it.toNewsTitleDto() }

    override suspend fun getNewsById(newsId: Int): FullNewsDto? =
        try {
            apiService.getNewsById(newsId).data.toDto()
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error fetching news by id: $newsId", e)
            null
        }

    override suspend fun searchNews(query: String): List<NewsTitleDto> =
        try {
            apiService.searchNews(query, null).data.map { it.toNewsTitleDto() }
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error searching news with query: $query", e)
            emptyList()
        }

        /*
    override suspend fun uploadImage(file: MultipartBody.Part, altText: String?): Int =
        try {
            val altTextPart = if (altText != null) {
                okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("text/plain"),
                    altText
                )
            } else {
                null
            }
            apiService.uploadImage(file, altTextPart).data.imageId
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error uploading image", e)
            throw e
        }

         */
    override suspend fun downloadImage(imageId: Int): ResponseBody? =
        try {
            apiService.getImageById("/api/images/by-id/$imageId")
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error downloading image: $imageId", e)
            null
        }

    override suspend fun getImageBitmap(imageId: Int?): android.graphics.Bitmap? {
        return try {
            if (imageId == null) return null
            val responseBody = downloadImage(imageId)
            responseBody?.byteStream()?.use { inputStream ->
                android.graphics.BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error getting image bitmap: $imageId", e)
            null
        }
    }

    override fun getLikedNews(): Flow<List<NewsTitleDto>> =
        likedNewsDao.getLikedNews().map { entities ->
            entities.map {
                NewsTitleDto(
                    id = it.newsId,
                    title = it.title,
                    shortDescription = it.shortDescription,
                )
            }
        }

    override fun isNewsLiked(newsId: Int): Flow<Boolean> = likedNewsDao.isNewsLiked(newsId)

    override suspend fun likeNews(
        newsId: Int,
        categoryId: Int,
        title: String,
        shortDescription: String,
    ) {
        likedNewsDao.likeNews(
            LikedNewsEntity(
                newsId = newsId,
                categoryId = categoryId,
                title = title,
                shortDescription = shortDescription,
            ),
        )
    }

    override suspend fun unlikeNews(newsId: Int) {
        likedNewsDao.unlikeNewsById(newsId)
    }
}
