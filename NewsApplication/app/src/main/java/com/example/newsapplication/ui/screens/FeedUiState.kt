package com.example.newsapplication.ui.screens

import com.example.newsapplication.data.dto.CategoryDto

data class FeedUiState(
    val categories: List<CategoryDto> = emptyList(),
    val selectedCategoryIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    // in case the loading did not succeed
    val errorMessage: String? = null,
  /*
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
   */
)
