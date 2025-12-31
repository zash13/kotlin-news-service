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
class FeedViewModel
    @Inject
    constructor(
        private val repository: INewsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
        val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

        init {
            resetFeedSelecets()
        }

        fun resetFeedSelecets() {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            viewModelScope.launch {
                try {
                    android.util.Log.d("NewsViewModel", "Fetching categories...")
                    val categories = repository.getCategories()
                    android.util.Log.d("NewsViewModel", "Received ${categories.size} categories")
                    _uiState.value =
                        FeedUiState(
                            categories = categories,
                            isLoading = false,
                        )
                } catch (e: Exception) {
                    android.util.Log.e("NewsViewModel", "Error loading categories", e)
                    _uiState.value =
                        FeedUiState(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load categories",
                        )
                }
            }
        }

        fun toggleCategorySelection(categoryId: Int) {
            val currentSelections = _uiState.value.selectedCategoryIds.toMutableSet()

            if (currentSelections.contains(categoryId)) {
                currentSelections.remove(categoryId)
            } else {
                currentSelections.add(categoryId)
            }

            _uiState.value = _uiState.value.copy(selectedCategoryIds = currentSelections)
        }

        fun clearSelections() {
            _uiState.value = _uiState.value.copy(selectedCategoryIds = emptySet())
        }

        fun saveUserCategories() {
            viewModelScope.launch {
                repository.setUserCategories(_uiState.value.selectedCategoryIds)
            }
        }
    }
