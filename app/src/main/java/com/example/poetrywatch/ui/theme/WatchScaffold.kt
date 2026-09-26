package com.example.poetrywatch.ui.theme

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.poetrywatch.data.preferences.ScreenShape

/**
 * 手表屏幕适配容器（汉克米风格）
 *
 * 形状来自用户在首次启动时选择的 [ScreenProfile]：
 * - [ScreenShape.ROUND]  圆形表盘：内容收进中心方形区域，四角留白，避免文字被表壳裁切
 * - [ScreenShape.SQUARE] 方形表盘：铺满屏幕，只留少量安全边距
 * - [ScreenShape.WIDE]   长方形表盘：左右留白，避免长条屏幕两侧被裁
 * - [ScreenShape.AUTO]   自动：读取系统 isScreenRound 标志
 *
 * 安全区档位（SafeArea）统一控制内缩距离，用户觉得文字被吃掉时可以调大。
 */
@Composable
fun WatchScaffold(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val profile = LocalScreenProfile.current
    val isRound = when (profile.shape) {
        ScreenShape.ROUND -> true
        ScreenShape.SQUARE, ScreenShape.WIDE -> false
        ScreenShape.AUTO -> systemIsRound()
    }
    val inset = profile.safeArea.insetDp.dp

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // 圆形屏：中心方形 + 圆角裁剪 + 安全区 padding
            isRound -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(inset)
                    .clip(RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                content()
            }

            // 长方形屏：左右留白多一些
            profile.shape == ScreenShape.WIDE -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = inset + 8.dp, vertical = inset / 2),
                contentAlignment = Alignment.Center
            ) {
                content()
            }

            // 方形屏：填满，只留少量边距
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = inset / 2, vertical = inset / 3),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

/** 读取系统圆屏标志；Configuration.isScreenRound 需要 API 23，低版本直接按方屏处理 */
@Composable
private fun systemIsRound(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        LocalConfiguration.current.isScreenRound
    } else {
        false
    }
