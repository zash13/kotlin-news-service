package com.example.newsapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.newsapplication.data.dto.CategoryDto
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun feedScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    feedContent(
        uiState = uiState,
        onCategorySelected = { categoryId ->
            viewModel.toggleCategorySelection(categoryId)
        },
        modifier = modifier,
    )
}

@Composable
fun feedContent(
    uiState: FeedUiState,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header
        Text(
            text = "Select Your Interests",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Error loading categories",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                    Button(
                        onClick = { /* Retry logic here */ },
                    ) {
                        Text("Retry")
                    }
                }
            }
        } else {
            categoryGrid(
                categories = uiState.categories,
                selectedCategoryIds = uiState.selectedCategoryIds,
                onCategorySelected = onCategorySelected,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
            )

            Button(
                onClick = {
                    println("Continue with selected categories: ${uiState.selectedCategoryIds}")
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                enabled = uiState.selectedCategoryIds.isNotEmpty(),
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
fun categoryGrid(
    categories: List<CategoryDto>,
    selectedCategoryIds: Set<Int>,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        items(categories) { category ->
            categoryItem(
                category = category,
                isSelected = selectedCategoryIds.contains(category.id),
                onSelected = { onCategorySelected(category.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun categoryItem(
    category: CategoryDto,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .clickable { onSelected() }
                .height(100.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = category.name,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )

                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelected() },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
    }
}
