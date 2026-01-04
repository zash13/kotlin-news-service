package com.example.newsapplication.network
import com.example.newsapplication.data.models.CategoriesResponse
import com.example.newsapplication.data.models.CategoryApiModel
import com.example.newsapplication.data.models.CreateNewsRequest
import com.example.newsapplication.data.models.CreateNewsResponse
import com.example.newsapplication.data.models.CreatedNewsData
import com.example.newsapplication.data.models.EndpointInfo
import com.example.newsapplication.data.models.FullNews
import com.example.newsapplication.data.models.FullNewsResponse
import com.example.newsapplication.data.models.ImageInfoResponse
import com.example.newsapplication.data.models.MultiCategoriesRequest
import com.example.newsapplication.data.models.MultiCategoriesTitlesResponse
import com.example.newsapplication.data.models.MultiCategoryNewsItem
import com.example.newsapplication.data.models.NewsByIdResponse
import com.example.newsapplication.data.models.NewsTitle
import com.example.newsapplication.data.models.NewsTitlesResponse
import com.example.newsapplication.data.models.RootResponse
import com.example.newsapplication.data.models.SearchNewsRequest
import com.example.newsapplication.data.models.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

// base url is configured in app/src/main/res/values/strings.xml
// retrofit instance is provided by hilt di in networkmodule

// interface

interface NewsApiService {
    @GET("/health")
    suspend fun checkHealth(): String

    @GET("/")
    suspend fun getRoot(): RootResponse

    @GET("/api/categories/")
    suspend fun getCategories(): CategoriesResponse

    @POST("/api/news/")
    suspend fun createNews(
        @Body request: CreateNewsRequest,
    ): CreateNewsResponse

    @GET("/api/news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("limit") limit: Int? = null,
    ): NewsTitlesResponse

    @GET("/api/news/by-category/{categoryId}/titles")
    suspend fun getTitlesByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("limit") limit: Int? = null,
    ): NewsTitlesResponse

    @GET("/api/news/newest/titles")
    suspend fun getNewestTitles(
        @Query("limit") limit: Int? = null,
    ): NewsTitlesResponse

    @GET("/api/news/{newsId}")
    suspend fun getNewsById(
        @Path("newsId") newsId: Int,
    ): NewsByIdResponse

    @GET("/api/news/newest/full")
    suspend fun getNewestFull(
        @Query("limit") limit: Int? = null,
    ): FullNewsResponse

    @GET("/api/news/by-category/{categoryId}/full")
    suspend fun getFullByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("limit") limit: Int? = null,
    ): FullNewsResponse

    @Multipart
    @POST("/api/images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("alt_text") altText: okhttp3.RequestBody? = null,
    ): UploadImageResponse

    @GET
    suspend fun getImageById(
        @Url url: String,
    ): okhttp3.ResponseBody
}
