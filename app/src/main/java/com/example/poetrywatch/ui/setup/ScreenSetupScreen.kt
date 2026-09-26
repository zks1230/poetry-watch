package com.example.poetrywatch.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poetrywatch.data.preferences.SafeArea
import com.example.poetrywatch.data.preferences.ScreenProfile
import com.example.poetrywatch.data.preferences.ScreenShape
import com.example.poetrywatch.ui.theme.LocalPoetryColors
import com.example.poetrywatch.ui.theme.LocalScreenProfile
import com.example.poetrywatch.ui.theme.WatchScaffold

/**
 * 屏幕适配向导（汉克米应用商店风格）：先让用户选自己的表盘形状，再选安全区大小。
 * 设置页里可以重新打开（showCancel = true）。
 */
@Composable
fun ScreenSetupScreen(
    initial: ScreenProfile = ScreenProfile.DEFAULT,
    showCancel: Boolean = false,
    onDone: (ScreenProfile) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var shapeName by rememberSaveable { mutableStateOf(initial.shape.name) }
    var safeAreaName by rememberSaveable { mutableStateOf(initial.safeArea.name) }
    val shape = runCatching { ScreenShape.valueOf(shapeName) }.getOrDefault(ScreenShape.AUTO)
    val safeArea = runCatching { SafeArea.valueOf(safeAreaName) }.getOrDefault(SafeArea.NORMAL)
    val draft = ScreenProfile(shape = shape, safeArea = safeArea)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (step == 0) "屏幕适配 1/2" else "屏幕适配 2/2",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (step == 0) {
            Text(
                text = "你的手表屏幕是哪一种？\n选错了文字会被表壳裁掉。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            ScreenShape.entries.forEach { option ->
                OptionChip(
                    title = option.label,
                    hint = option.hint,
                    selected = shapeName == option.name,
                    onClick = { shapeName = option.name }
                )
            }
            Button(
                onClick = { step = 1 },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) { Text("下一步") }
        } else {
            Text(
                text = "安全区越大，边缘留白越多。\n下面的预览里文字不被切掉就说明合适。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            ScreenPreview(draft)
            SafeArea.entries.forEach { option ->
                OptionChip(
                    title = option.label,
                    hint = option.hint,
                    selected = safeAreaName == option.name,
                    onClick = { safeAreaName = option.name }
                )
            }
            Button(
                onClick = { onDone(draft) },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) { Text(if (showCancel) "保存" else "开始使用") }
            OutlinedButton(
                onClick = { step = 0 },
                modifier = Modifier.fillMaxWidth()
            ) { Text("上一步") }
        }

        if (showCancel && onCancel != null) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) { Text("返回设置") }
        }
    }
}

@Composable
private fun OptionChip(
    title: String,
    hint: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(
                    text = title + if (selected) "  ✓" else "",
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                Text(text = hint, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}

/** 用当前选择真实渲染一小块界面，用户能直接看到文字有没有被裁掉 */
@Composable
private fun ScreenPreview(profile: ScreenProfile) {
    val colors = LocalPoetryColors.current
    val isWide = profile.shape == ScreenShape.WIDE
    val previewWidth = if (isWide) 180.dp else 128.dp
    val previewHeight = if (isWide) 108.dp else 128.dp
    val outerShape = when (profile.shape) {
        ScreenShape.ROUND -> RoundedCornerShape(percent = 50)
        else -> RoundedCornerShape(16.dp)
    }

    Box(
        modifier = Modifier
            .width(previewWidth)
            .height(previewHeight)
            .clip(outerShape)
            .background(colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "预览",
            fontSize = 9.sp,
            color = colors.secondary,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp)
        )
        CompositionLocalProvider(LocalScreenProfile provides profile) {
            WatchScaffold(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "静夜思",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = "床前明月光，疑是地上霜。",
                        fontSize = 9.sp,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(15.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("开始检测", fontSize = 9.sp, color = colors.background)
                    }
                }
            }
        }
    }
}
