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
    val isLiked: Boolean = false,
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
                        repository.isNewsLiked(newsId).collect { isLiked ->
                            _uiState.value =
                                NewsDetailUiState(
                                    news = news,
                                    isLoading = false,
                                    isLiked = isLiked,
                                )
                        }
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

        fun toggleLike() {
            val news = _uiState.value.news ?: return
            
            viewModelScope.launch {
                if (_uiState.value.isLiked) {
                    repository.unlikeNews(news.id)
                } else {
                    val categoryId = news.categories.firstOrNull()?.id ?: 0
                    repository.likeNews(
                        newsId = news.id,
                        categoryId = categoryId,
                        title = news.title,
                        shortDescription = news.description.take(200),
                    )
                }
                _uiState.value = _uiState.value.copy(isLiked = !_uiState.value.isLiked)
            }
        }
    }
