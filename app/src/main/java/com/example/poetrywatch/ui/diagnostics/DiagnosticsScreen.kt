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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.poetrywatch.ui.MainViewModel
import com.example.poetrywatch.ui.theme.LocalPoetryColors
import com.example.poetrywatch.util.CrashLogger

/**
 * 诊断信息页：把设备 / 屏幕 / 数据量 / 最近崩溃日志集中显示出来，
 * App 出问题时截图这一页就够了。
 */
@Composable
fun DiagnosticsScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    diagnosticsViewModel: DiagnosticsViewModel = hiltViewModel()
) {
    val colors = LocalPoetryColors.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val profile by mainViewModel.screenProfile.collectAsState()
    val counts by diagnosticsViewModel.counts.collectAsState()
    val deviceInfo = rememberDeviceInfo()

    var crashText by remember { mutableStateOf(CrashLogger.lastCrash(context)) }

    val report = buildString {
        appendLine("== 设备 ==")
        appendLine(deviceInfo)
        appendLine("屏幕适配：${profile.shape.label} / 安全区 ${profile.safeArea.label}")
        appendLine("诗词数量：${counts.first}，学习记录：${counts.second}")
        appendLine()
        appendLine("== 最近一次崩溃 ==")
        appendLine(crashText ?: "（没有崩溃记录）")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "诊断信息",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
        Text(
            text = report,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .background(colors.surface)
                .padding(6.dp)
        )
        Button(
            onClick = { clipboard.setText(AnnotatedString(report)) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("复制全部信息") }
        OutlinedButton(
            onClick = {
                CrashLogger.clear(context)
                crashText = null
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("清除崩溃日志") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("返回") }
    }
}

/** 设备 / 屏幕描述 */
@Composable
fun rememberDeviceInfo(): String {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        val metrics = context.resources.displayMetrics
        buildString {
            appendLine("Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
            appendLine("机型：${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
            appendLine("屏幕：${configuration.screenWidthDp}×${configuration.screenHeightDp} dp / ${metrics.widthPixels}×${metrics.heightPixels} px")
            appendLine("密度：${metrics.density}")
            appendLine("系统圆屏标志：${if (android.os.Build.VERSION.SDK_INT >= 23) configuration.isScreenRound else "低版本不支持"}")
            append("ABI：${android.os.Build.SUPPORTED_ABIS.joinToString()}")
        }
    }
}
