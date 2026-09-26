package com.example.poetrywatch.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.poetrywatch.data.preferences.ScreenProfile

/**
 * 当前生效的屏幕适配方案。
 * 由首次启动向导 / 设置页写入 DataStore，再由 WatchScaffold 读取。
 */
val LocalScreenProfile = staticCompositionLocalOf { ScreenProfile.DEFAULT }
