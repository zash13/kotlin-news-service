package com.example.newsapplication.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryApiModel(
    @SerialName(value = "category_id")
    val categoryId: String,
    @SerialName(value = "category_name")
    val categoryName: String,
)

@Serializable
data class CategoriesResponse(
    val success: Boolean,
    val message: String,
    val data: List<CategoryApiModel>,
    val timestamp: String
)
