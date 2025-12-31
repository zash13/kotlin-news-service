
package com.example.newsapplication.network
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.data.models.CategoryApiModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

// put your own ip in here
private const val BASE_URL =
    "http://10.242.219.157:8000"

private val retrofit =
    Retrofit
        .Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

interface NewsApiService {
    @GET("/health")
    suspend fun checkHealth(): String

    @GET("/api/categories/")
    suspend fun getCategories(): CategoriesResponse
}

object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}
