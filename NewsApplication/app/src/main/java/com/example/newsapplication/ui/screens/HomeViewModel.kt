package com.example.newsapplication.ui.screens

import androidx.hilt.navigation.compose.hiltViewModel
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

        private val _searchQuery = MutableStateFlow("")
        val searchQuery = _searchQuery.asStateFlow()

        init {
            loadCategories()
            loadNews()
        }

        private fun loadCategories() {
            viewModelScope.launch {
                try {
                    val categories = repository.getCategories()
                    val userCategories = repository.getUserCategories().value
                    val selectedIds = userCategories.map { it.categoryId }.toSet()

                    _uiState.value = _uiState.value.copy(
                        newsCategories = categories,
                        selectedCategoryIds = selectedIds
                    )
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Error loading categories", e)
                }
            }
        }

        fun loadNews(searchString: String? = "") {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, errorLoading = false)

            viewModelScope.launch {
                try {
                    val newsTitles = if (!searchString.isNullOrBlank()) {
                        repository.searchNews(searchString)
                    } else {
                        val userCategories = repository.getUserCategories().value
                        val categoryIds = userCategories.map { it.categoryId }.toSet()

                        if (categoryIds.isEmpty()) {
                            repository.getNewsTitles()
                        } else {
                            repository.getNewsTitles(categoryIds)
                        }
                    }

                    _uiState.value =
                        _uiState.value.copy(
                            newsTitles = newsTitles,
                            isLoading = false,
                            errorMessage = null,
                            errorLoading = false,
                        )
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load news",
                            errorLoading = true,
                        )
                }
            }
        }

        fun onSearchQueryChanged(newValue: String) {
            val wasSearching = !_searchQuery.value.isNullOrBlank()
            val isSearching = !newValue.isNullOrBlank()
            
            _searchQuery.value = newValue
            
            if (wasSearching && !isSearching) {
                loadNews()
            }
        }

        fun onSearch() {
            loadNews(_searchQuery.value)
        }

        fun retry() {
            loadNews()
        }

        fun toggleCategorySelection(categoryId: Int) {
            val currentSelections = _uiState.value.selectedCategoryIds.toMutableSet()

            if (currentSelections.contains(categoryId)) {
                currentSelections.remove(categoryId)
            } else {
                currentSelections.add(categoryId)
            }

            _uiState.value = _uiState.value.copy(selectedCategoryIds = currentSelections)

            viewModelScope.launch {
                repository.setUserCategories(currentSelections)
                loadNews()
            }
        }
    }
