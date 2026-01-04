package com.example.newsapplication.data.mapper
import com.example.newsapplication.data.dto.CategoryDto
import com.example.newsapplication.data.dto.CategoryInfoDto
import com.example.newsapplication.data.dto.FullNewsDto
import com.example.newsapplication.data.dto.NewsTitleDto
import com.example.newsapplication.data.models.CategoryApiModel
import com.example.newsapplication.data.models.CategoryInfo
import com.example.newsapplication.data.models.FullNews
import com.example.newsapplication.data.models.MultiCategoryNewsItem
import com.example.newsapplication.data.models.NewsTitle

fun CategoryApiModel.toDto(): CategoryDto =
    CategoryDto(
        id = categoryId.toInt(),
        name = categoryName,
    )

fun CategoryInfo.toDto(): CategoryInfoDto =
    CategoryInfoDto(
        id = id,
        name = name,
    )

fun NewsTitle.toNewsTitleDto(): NewsTitleDto =
    NewsTitleDto(
        id = id,
        title = title,
        shortDescription = shortDescription,
        imageId = imageId,
        createAt = "",
    )

fun MultiCategoryNewsItem.toNewsTitleDto(): NewsTitleDto =
    NewsTitleDto(
        id = id,
        title = title,
        shortDescription = shortDescription,
        imageId = imageId,
        createAt = timestamp,
    )

fun FullNews.toDto(): FullNewsDto =
    FullNewsDto(
        id = id,
        title = title,
        description = description,
        categories = categories.map { it.toDto() },
        source = source,
        imageId = imageId,
        imageLocation = imageLocation,
        createdAt = created_at,
    )

