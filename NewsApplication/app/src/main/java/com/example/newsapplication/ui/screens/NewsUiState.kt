package com.example.newsapplication.ui.screens

import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.dto.NewsTitleDto

data class NewsUiState(
    val newsTitles: List<NewsTitleDto> = emptyList(),
    val newsCategories: List<CategoryDto> = emptyList(),
    val selectedCategoryIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorLoading: Boolean = false,
)
