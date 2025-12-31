package com.example.newsapplication.data.repository

import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.dto.NewsTitleDto
import com.example.newsapplication.data.mapper.toDto
import com.example.newsapplication.data.mapper.toNewsTitleDto
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.data.models.MultiCategoriesRequest
import com.example.newsapplication.data.models.UserCategory
import com.example.newsapplication.network.NewsApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface INewsRepository {
    suspend fun checkHealth(): String

    suspend fun getCategories(): List<CategoryDto>

    fun getUserCategories(): StateFlow<List<UserCategory>>

    suspend fun setUserCategories(categoryIds: Set<Int>)

    suspend fun getNewsTitles(categoryIds: Set<Int>): List<NewsTitleDto>
}

class NewsRepository(
    private val apiService: NewsApiService,
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

    override suspend fun getNewsTitles(categoryIds: Set<Int>): List<NewsTitleDto> {
        return if (categoryIds.isEmpty()) {
            apiService.getNewestTitles(null).data.map { it.toNewsTitleDto() }
        } else {
            val categories = getCategories()
            val categoryNames = categoryIds
                .mapNotNull { categoryId ->
                    categories.find { it.id == categoryId }?.name
                }

            if (categoryNames.isEmpty()) {
                apiService.getNewestTitles(null).data.map { it.toNewsTitleDto() }
            } else {
                val request = MultiCategoriesRequest(categories = categoryNames, limit_per_category = 5)
                apiService.getTitlesByMultipleCategories(request).data.map { it.toNewsTitleDto() }
            }
        }
    }
}
