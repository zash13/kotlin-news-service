package com.example.newsapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.data.dto.FullNewsDto
import com.example.newsapplication.data.repository.INewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailUiState(
    val news: FullNewsDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class NewsDetailViewModel
    @Inject
    constructor(
        private val repository: INewsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<NewsDetailUiState>(NewsDetailUiState(isLoading = true))
        val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

        fun loadNews(newsId: Int) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            viewModelScope.launch {
                try {
                    android.util.Log.d("NewsDetailViewModel", "Loading news details for id: $newsId")
                    val news = repository.getNewsById(newsId)

                    if (news != null) {
                        _uiState.value =
                            NewsDetailUiState(
                                news = news,
                                isLoading = false,
                            )
                    } else {
                        _uiState.value =
                            NewsDetailUiState(
                                isLoading = false,
                                errorMessage = "News not found",
                            )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("NewsDetailViewModel", "Error loading news details", e)
                    _uiState.value =
                        NewsDetailUiState(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load news",
                        )
                }
            }
        }
    }