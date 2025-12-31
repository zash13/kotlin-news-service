
package com.example.newsapplication.ui.screens

package com.example.newsapplication.ui.screens

import com.example.newsapplication.data.dto.NewsTitleDto

data class NewsUiState(
    val newsTitels: List<NewsTitleDto> = emptyList(),
    val isLoading: Boolean = false,
    // in case the loading did not succeed
    val errorMessage: String? = null,
    val errorLoading: Boolean = false,
)
)
