package com.example.newsapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapplication.ui.screens.homeScreen
import com.example.newsapplication.ui.screens.newsDetailScreen
import com.example.newsapplication.ui.theme.NewsApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsApplicationTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable(route = Screen.Home.route) {
                            homeScreen(
                                onNavigateToDetail = { newsId ->
                                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                                },
                            )
                        }
                        composable(route = Screen.NewsDetail.route) { backStackEntry ->
                            val newsId = backStackEntry.arguments?.getString("newsId")?.toIntOrNull() ?: 0
                            newsDetailScreen(
                                newsId = newsId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
) {
    data object Home : Screen("home")

    data object NewsDetail : Screen("newsDetail/{newsId}") {
        fun createRoute(newsId: Int): String = "newsDetail/$newsId"
    }
}
