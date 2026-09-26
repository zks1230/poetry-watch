package com.example.poetrywatch.ui.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poetrywatch.ui.theme.LocalPoetryColors

/**
 * 崩溃提示页：App 上次崩溃后重新打开时先显示这个，
 * 用户可以直接看到原因（并复制/截图发给开发者），而不是只有「已停止运行」。
 */
@Composable
fun CrashReportScreen(
    crashText: String,
    onContinue: () -> Unit,
    onClear: () -> Unit
) {
    val colors = LocalPoetryColors.current
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "上次运行崩溃了",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "下面是可以发给开发者的错误信息，右上角可以截图：",
            fontSize = 11.sp,
            color = colors.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = crashText,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .background(colors.surface)
                .padding(6.dp)
        )
        Button(
            onClick = {
                clipboard.setText(AnnotatedString(crashText))
                onContinue()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("复制并继续使用") }
        OutlinedButton(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth()
        ) { Text("清除日志并继续") }
    }
}
