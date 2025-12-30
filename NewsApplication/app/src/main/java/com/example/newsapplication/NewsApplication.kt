// NewsApplication.kt
package com.example.newsapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {
    // Optional: You can add initialization code here
    override fun onCreate() {
        super.onCreate()
    }
}
