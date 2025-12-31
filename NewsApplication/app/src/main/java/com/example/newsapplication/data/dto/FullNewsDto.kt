
package com.example.newsapplication.data.dto

data class CategoryInfoDto(
    val id: Int,
    val name: String,
)

data class FullNewsDto(
    val id: Int,
    val title: String,
    val description: String,
    val categories: List<CategoryInfoDto>,
    val source: String,
    val imageId: Int? = null,
    val createdAt: String,
)