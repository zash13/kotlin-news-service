package com.example.newsapplication.data.repository

import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.mapper.toDto
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.network.NewsApiService

interface NewsRepository {
    suspend fun checkHealth(): String

    suspend fun getCategories(): List<CategoryDto> // Changed to return DTOs, not API models
}

class NetworkNewsRepository(
    private val apiService: NewsApiService,
) : NewsRepository {
    override suspend fun checkHealth(): String = apiService.checkHealth()

    override suspend fun getCategories(): List<CategoryDto> = apiService.getCategories().data.map { it.toDto() }
}
