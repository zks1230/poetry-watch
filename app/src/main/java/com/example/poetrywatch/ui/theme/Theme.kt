package com.example.poetrywatch.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 可切换主题枚举 */
enum class ThemeType(val label: String) {
    MINIMAL("极简"),
    PAPER("宣纸"),
    SPRING("春"),
    SUMMER("夏"),
    AUTUMN("秋"),
    WINTER("冬")
}

/** 主题原始配色 */
data class PoetryColors(
    val background: Color,
    val surface: Color,
    val onBackground: Color,
    val onSurface: Color,
    val primary: Color,
    val secondary: Color
)

fun ThemeType.colors(): PoetryColors = when (this) {
    ThemeType.MINIMAL -> PoetryColors(
        background = Color(0xFF0F1115),
        surface = Color(0xFF1C1F27),
        onBackground = Color(0xFFECEDF0),
        onSurface = Color(0xFFD9DBE1),
        primary = Color(0xFFD9B35C),
        secondary = Color(0xFF8A93A6)
    )
    ThemeType.PAPER -> PoetryColors(
        background = Color(0xFFF5EFE0),
        surface = Color(0xFFEDE4CF),
        onBackground = Color(0xFF3D3830),
        onSurface = Color(0xFF544D42),
        primary = Color(0xFF8B6B3A),
        secondary = Color(0xFF9A9384)
    )
    ThemeType.SPRING -> PoetryColors(
        background = Color(0xFF12231E),
        surface = Color(0xFF1C352D),
        onBackground = Color(0xFFE6F2EC),
        onSurface = Color(0xFFC6DCD2),
        primary = Color(0xFF7FBF9A),
        secondary = Color(0xFF6E8F80)
    )
    ThemeType.SUMMER -> PoetryColors(
        background = Color(0xFF131A2B),
        surface = Color(0xFF1E2840),
        onBackground = Color(0xFFEDF1FA),
        onSurface = Color(0xFFCDD6EA),
        primary = Color(0xFF6FA3EF),
        secondary = Color(0xFF7E8CA8)
    )
    ThemeType.AUTUMN -> PoetryColors(
        background = Color(0xFF241813),
        surface = Color(0xFF35261E),
        onBackground = Color(0xFFF6EDE4),
        onSurface = Color(0xFFE0D2C4),
        primary = Color(0xFFD9A05B),
        secondary = Color(0xFFA0886F)
    )
    ThemeType.WINTER -> PoetryColors(
        background = Color(0xFF1C2630),
        surface = Color(0xFF283742),
        onBackground = Color(0xFFEDF2F6),
        onSurface = Color(0xFFCFD9E0),
        primary = Color(0xFF9FC4DE),
        secondary = Color(0xFF8296A5)
    )
}

/** 应用到 Compose 的配色 */
data class AppColors(
    val background: Color,
    val surface: Color,
    val onBackground: Color,
    val onSurface: Color,
    val primary: Color,
    val secondary: Color
) {
    companion object {
        fun from(colors: PoetryColors) = AppColors(
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.onBackground,
            onSurface = colors.onSurface,
            primary = colors.primary,
            secondary = colors.secondary
        )
    }
}

/** 全局主题配色本地对象 */
val LocalPoetryColors = staticCompositionLocalOf { ThemeType.MINIMAL.colors().let { AppColors.from(it) } }

@Composable
fun PoetryWatchTheme(themeType: ThemeType, content: @Composable () -> Unit) {
    val colors = AppColors.from(themeType.colors())
    CompositionLocalProvider(LocalPoetryColors provides colors) {
        content()
    }
}