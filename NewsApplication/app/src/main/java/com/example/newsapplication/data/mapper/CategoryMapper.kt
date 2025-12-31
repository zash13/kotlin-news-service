package com.example.newsapplication.data.mapper
import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.dto.NewsTitleDto
import com.example.newsapplication.data.models.CategoryApiModel
import com.example.newsapplication.data.models.MultiCategoryNewsItem
import com.example.newsapplication.data.models.NewsTitle

fun CategoryApiModel.toDto(): CategoryDto =
    CategoryDto(
        id = categoryId.toInt(),
        name = categoryName,
    )

fun NewsTitle.toNewsTitleDto(): NewsTitleDto =
    NewsTitleDto(
        id = id,
        title = title,
        createAt = "",
    )

fun MultiCategoryNewsItem.toNewsTitleDto(): NewsTitleDto =
    NewsTitleDto(
        id = id,
        title = title,
        createAt = timestamp,
    )
