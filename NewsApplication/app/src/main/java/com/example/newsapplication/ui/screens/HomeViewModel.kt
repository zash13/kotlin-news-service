
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
        }
    }
