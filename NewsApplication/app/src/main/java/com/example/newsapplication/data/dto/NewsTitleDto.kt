

package com.example.newsapplication.data.dto

data class NewsTitleDto(
    val id: Int,
    val title: String,
    val shortDescription: String? = null,
    val description: String? = null,
    val imageId: Int? = null,
    val createAt: String = "",
)
