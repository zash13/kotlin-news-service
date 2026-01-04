package com.example.newsapplication.ui.screens

import android.content.Context
import com.example.newsapplication.R

object ImageUtils {
    fun getImageUrl(
        context: Context,
        imageId: Int?,
    ): String? {
        if (imageId == null) return null
        val baseUrl = context.getString(R.string.base_url)
        return "$baseUrl/api/images/by-id/$imageId"
    }
}
