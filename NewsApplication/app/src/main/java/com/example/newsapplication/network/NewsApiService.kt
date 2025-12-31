
package com.example.newsapplication.network
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.data.models.CategoryApiModel
import com.example.newsapplication.data.models.CreateNewsRequest
import com.example.newsapplication.data.models.CreateNewsResponse
import com.example.newsapplication.data.models.CreatedNewsData
import com.example.newsapplication.data.models.EndpointInfo
import com.example.newsapplication.data.models.FullNews
import com.example.newsapplication.data.models.FullNewsResponse
import com.example.newsapplication.data.models.MultiCategoriesRequest
import com.example.newsapplication.data.models.MultiCategoriesTitlesResponse
import com.example.newsapplication.data.models.MultiCategoryNewsItem
import com.example.newsapplication.data.models.NewsByIdResponse
import com.example.newsapplication.data.models.NewsTitle
import com.example.newsapplication.data.models.NewsTitlesResponse
import com.example.newsapplication.data.models.RootResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// put your own ip in here
private const val BASE_URL =
    "http://10.242.219.157:8000"

private val retrofit =
    Retrofit
        .Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}

// Interface

interface NewsApiService {
    @GET("/health")
    suspend fun checkHealth(): String

    @GET("/api/categories/")
    suspend fun getCategories(): CategoriesResponse

    @GET("/")
    suspend fun getRoot(): RootResponse

    @POST("/api/news/")
    suspend fun createNews(
        @Body request: CreateNewsRequest,
    ): CreateNewsResponse

    @GET("/api/news/by-category/{category}/titles")
    suspend fun getTitlesByCategory(
        @Path("category") category: String,
        @Query("limit") limit: Int? = null,
    ): NewsTitlesResponse

    @POST("/api/news/by-multiple-categories/titles")
    suspend fun getTitlesByMultipleCategories(
        @Body request: MultiCategoriesRequest,
    ): MultiCategoriesTitlesResponse

    @GET("/api/news/newest/titles")
    suspend fun getNewestTitles(
        @Query("limit") limit: Int? = null,
    ): NewsTitlesResponse

    @GET("/api/news/{news_id}")
    suspend fun getNewsById(
        @Path("news_id") newsId: Int,
    ): NewsByIdResponse

    @GET("/api/news/newest/full")
    suspend fun getNewestFull(
        @Query("limit") limit: Int? = null,
    ): FullNewsResponse

    @GET("/api/news/by-category/{category}/full")
    suspend fun getFullByCategory(
        @Path("category") category: String,
        @Query("limit") limit: Int? = null,
    ): FullNewsResponse
}
