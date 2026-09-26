package com.example.poetrywatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poetrywatch.data.preferences.ScreenProfile
import com.example.poetrywatch.ui.detail.DetailScreen
import com.example.poetrywatch.ui.diagnostics.CrashReportScreen
import com.example.poetrywatch.ui.diagnostics.DiagnosticsScreen
import com.example.poetrywatch.ui.home.HomeScreen
import com.example.poetrywatch.ui.library.LibraryScreen
import com.example.poetrywatch.ui.manage.ManageScreen
import com.example.poetrywatch.ui.practice.PracticeScreen
import com.example.poetrywatch.ui.quiz.QuizScreen
import com.example.poetrywatch.ui.settings.SettingsScreen
import com.example.poetrywatch.ui.setup.ScreenSetupScreen
import com.example.poetrywatch.ui.theme.LocalPoetryColors
import com.example.poetrywatch.ui.theme.LocalScreenProfile
import com.example.poetrywatch.ui.theme.UniversalTheme
import com.example.poetrywatch.ui.theme.WatchScaffold
import com.example.poetrywatch.util.CrashLogger

@Composable
fun UniversalApp(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val theme by mainViewModel.theme.collectAsState()
    val screenProfile by mainViewModel.screenProfile.collectAsState()
    val setupCompleted by mainViewModel.setupCompleted.collectAsState()
    val context = LocalContext.current

    // 上次崩溃过 → 先把错误信息摆出来（用户可以直接截图/复制）
    var crashText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        runCatching {
            if (CrashLogger.hasUnseenCrash(context)) {
                crashText = CrashLogger.lastCrash(context)
                CrashLogger.markSeen(context)
            }
        }
    }

    UniversalTheme(themeType = theme) {
        CompositionLocalProvider(LocalScreenProfile provides screenProfile) {
            val crash = crashText
            when {
                crash != null -> CrashReportScreen(
                    crashText = crash,
                    onContinue = { crashText = null },
                    onClear = {
                        CrashLogger.clear(context)
                        crashText = null
                    }
                )

                // 还没读到设置：短暂空白，避免先闪一下向导
                setupCompleted == null -> Box(
                    modifier = Modifier.fillMaxSize().background(LocalPoetryColors.current.background)
                )

                // 首次启动：先选屏幕形状 / 安全区（汉克米风格）
                setupCompleted == false -> Box(
                    modifier = Modifier.fillMaxSize().background(LocalPoetryColors.current.background)
                ) {
                    // 向导本身也用当前（默认）适配方案排版，圆屏上不会顶到表壳
                    WatchScaffold(modifier = Modifier.fillMaxSize()) {
                        ScreenSetupScreen(
                            initial = screenProfile,
                            showCancel = false,
                            onDone = { mainViewModel.saveScreenProfile(it) }
                        )
                    }
                }

                else -> MainNavHost(mainViewModel)
            }
        }
    }
}

@Composable
private fun MainNavHost(mainViewModel: MainViewModel) {
    val colors = LocalPoetryColors.current
    val screenProfile by mainViewModel.screenProfile.collectAsState()
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
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    mainViewModel = mainViewModel,
                    onOpenScreenSetup = { navController.navigate(Routes.SETUP) },
                    onOpenDiagnostics = { navController.navigate(Routes.DIAGNOSTICS) }
                )
            }
            composable(Routes.SETUP) {
                ScreenSetupScreen(
                    initial = screenProfile,
                    showCancel = true,
                    onDone = { profile: ScreenProfile ->
                        mainViewModel.saveScreenProfile(profile)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Routes.DIAGNOSTICS) {
                DiagnosticsScreen(
                    mainViewModel = mainViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
