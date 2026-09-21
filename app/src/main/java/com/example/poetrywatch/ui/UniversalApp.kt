package com.example.poetrywatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poetrywatch.ui.detail.DetailScreen
import com.example.poetrywatch.ui.home.HomeScreen
import com.example.poetrywatch.ui.library.LibraryScreen
import com.example.poetrywatch.ui.manage.ManageScreen
import com.example.poetrywatch.ui.practice.PracticeScreen
import com.example.poetrywatch.ui.quiz.QuizScreen
import com.example.poetrywatch.ui.settings.SettingsScreen
import com.example.poetrywatch.ui.theme.LocalPoetryColors
import com.example.poetrywatch.ui.theme.UniversalTheme
import com.example.poetrywatch.ui.theme.WatchScaffold

@Composable
fun UniversalApp(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val theme by mainViewModel.theme.collectAsState()
    UniversalTheme(themeType = theme) {
        val colors = LocalPoetryColors.current
        val navController = rememberNavController()

        WatchScaffold(
            modifier = Modifier.background(colors.background)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.HOME) { HomeScreen(navController) }
                composable(Routes.LIBRARY) { LibraryScreen(navController) }
                composable(
                    route = Routes.DETAIL,
                    arguments = listOf(navArgument("poemId") { type = NavType.StringType })
                ) { DetailScreen(navController) }
                composable(Routes.PRACTICE) { PracticeScreen(navController) }
                composable(
                    route = Routes.QUIZ,
                    arguments = listOf(navArgument("poemId") { type = NavType.StringType })
                ) { QuizScreen(navController) }
                composable(Routes.MANAGE) { ManageScreen() }
                composable(Routes.SETTINGS) { SettingsScreen() }
            }
        }
    }
}
