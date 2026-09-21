package com.example.poetrywatch.ui.theme

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

/**
 * 汉克米风格的屏幕适配容器
 *
 * 圆形表盘：内容被约束在中心方形区域，四角留白，避免文字被边缘裁切。
 * 方形表盘：内容居中，四周少量 padding。
 */
@Composable
fun WatchScaffold(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isRound = LocalConfiguration.current.isScreenRound

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isRound) {
            // 圆形屏：中心方形 + 圆角裁剪 + 额外 padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        } else {
            // 方形屏：直接填满，少量 padding 让按钮不贴边
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
