package com.example.poetrywatch.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/** 将自研主题配色映射为 Material3 ColorScheme */
fun toM3Scheme(colors: AppColors): ColorScheme = darkColorScheme(
    background = colors.background,
    surface = colors.surface,
    surfaceVariant = colors.surface,
    onBackground = colors.onBackground,
    onSurface = colors.onSurface,
    onSurfaceVariant = colors.secondary,
    primary = colors.primary,
    secondary = colors.secondary,
    onPrimary = colors.onBackground,
    onSecondary = colors.onBackground,
    onSecondaryContainer = colors.onSurface
)

@Composable
fun UniversalTheme(themeType: ThemeType, content: @Composable () -> Unit) {
    val colors = AppColors.from(themeType.colors())
    CompositionLocalProvider(LocalPoetryColors provides colors) {
        MaterialTheme(colorScheme = toM3Scheme(colors)) { content() }
    }
}
