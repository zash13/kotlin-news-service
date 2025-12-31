package com.example.newsapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.data.repository.INewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: INewsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NewsUiState(isLoading = true))
        val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

        init {
            loadNews()
        }

        fun loadNews() {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, errorLoading = false)

            viewModelScope.launch {
                try {
                    android.util.Log.d("HomeViewModel", "Loading news titles...")
                    val userCategories = repository.getUserCategories().value
                    val categoryIds = userCategories.map { it.categoryId }.toSet()

                    android.util.Log.d("HomeViewModel", "User selected categories: $categoryIds")

                    val newsTitles = repository.getNewsTitles(categoryIds)
                    android.util.Log.d("HomeViewModel", "Received ${newsTitles.size} news titles")

                    _uiState.value =
                        NewsUiState(
                            newsTitels = newsTitles,
                            isLoading = false,
                            errorMessage = null,
                            errorLoading = false,
                        )
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Error loading news", e)
                    _uiState.value =
                        NewsUiState(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load news",
                            errorLoading = true,
                        )
                }
            }
        }

        fun retry() {
            loadNews()
        }
    }
