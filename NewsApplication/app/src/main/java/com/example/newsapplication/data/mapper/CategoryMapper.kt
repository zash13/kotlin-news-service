
package com.example.newsapplication.data.mapper
import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.models.CategoryApiModel

fun CategoryApiModel.toDto(): CategoryDto =
    CategoryDto(
        id = categoryId.toInt(),
        name = categoryName,
    )
