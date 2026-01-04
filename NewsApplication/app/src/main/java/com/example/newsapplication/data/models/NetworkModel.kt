package com.example.newsapplication.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryApiModel(
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("category_name")
    val categoryName: String,
)

@Serializable
data class CategoriesResponse(
    val success: Boolean,
    val message: String,
    val data: List<CategoryApiModel>,
    val timestamp: String? = null,
)

@Serializable
data class CreateNewsRequest(
    val title: String,
    val description: String,
    @SerialName("short_description")
    val shortDescription: String? = null,
    @SerialName("category_ids")
    val categoryIds: List<Int>,
    val source: String,
    @SerialName("image_id")
    val imageId: Int? = null,
)

@Serializable
data class CreatedNewsData(
    val id: Int,
    val title: String,
    val created_at: String,
)

@Serializable
data class CreateNewsResponse(
    val success: Boolean,
    val message: String,
    val data: CreatedNewsData,
    val timestamp: String? = null,
)

@Serializable
data class NewsTitle(
    val id: Int,
    val title: String,
    @SerialName("short_description")
    val shortDescription: String? = null,
    @SerialName("image_id")
    val imageId: Int? = null,
)

@Serializable
data class NewsTitlesResponse(
    val success: Boolean,
    val message: String,
    val data: List<NewsTitle>,
    val timestamp: String? = null,
)

@Serializable
data class CategoryInfo(
    @SerialName("category_id")
    val id: Int,
    @SerialName("category_name")
    val name: String,
)

@Serializable
data class FullNews(
    val id: Int,
    val title: String,
    val description: String,
    val categories: List<CategoryInfo>,
    val source: String,
    val created_at: String,
    @SerialName("image_id")
    val imageId: Int? = null,
    @SerialName("image_location")
    val imageLocation: String? = null,
)

@Serializable
data class NewsByIdResponse(
    val success: Boolean,
    val message: String,
    val data: FullNews,
    val timestamp: String? = null,
)

@Serializable
data class FullNewsResponse(
    val success: Boolean,
    val message: String,
    val data: List<FullNews>,
    val timestamp: String? = null,
)

@Serializable
data class MultiCategoryNewsItem(
    val id: Int,
    val title: String,
    @SerialName("short_description")
    val shortDescription: String? = null,
    @SerialName("image_id")
    val imageId: Int? = null,
    val categories: List<CategoryInfo>,
    val timestamp: String,
    val source: String,
)

@Serializable
data class MultiCategoriesTitlesResponse(
    val success: Boolean,
    val message: String,
    val data: List<MultiCategoryNewsItem>,
    val timestamp: String? = null,
)

@Serializable
data class MultiCategoriesRequest(
    @SerialName("category_ids")
    val categoryIds: List<Int>,
    val limit_per_category: Int? = null,
)

@Serializable
data class EndpointInfo(
    val news: Map<String, String>,
    val health: String,
    val docs: String,
    val redoc: String,
)

@Serializable
data class RootResponse(
    val message: String,
    val version: String,
    val endpoints: EndpointInfo,
)

@Serializable
data class SearchNewsRequest(
    val q: String,
    val limit: Int? = null,
)

@Serializable
data class UploadImageRequest(
    val file: String,
    @SerialName("alt_text")
    val altText: String? = null,
)

@Serializable
data class UploadedImageData(
    @SerialName("image_id")
    val imageId: Int,
    val location: String,
    val filename: String,
    @SerialName("alt_text")
    val altText: String? = null,
)

@Serializable
data class UploadImageResponse(
    val success: Boolean,
    val message: String,
    val data: UploadedImageData,
    val timestamp: String? = null,
)

@Serializable
data class ImageInfoResponse(
    val success: Boolean,
    val message: String,
    val data: UploadedImageData,
    val timestamp: String? = null,
)
