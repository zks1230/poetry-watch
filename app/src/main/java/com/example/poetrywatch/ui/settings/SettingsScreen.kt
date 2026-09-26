package com.example.poetrywatch.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poetrywatch.ui.MainViewModel
import com.example.poetrywatch.ui.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    onOpenScreenSetup: () -> Unit = {},
    onOpenDiagnostics: () -> Unit = {}
) {
    val currentTheme by mainViewModel.theme.collectAsState()
    val profile by mainViewModel.screenProfile.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("设置") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                SectionTitle("屏幕适配")
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenScreenSetup,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "屏幕形状：${profile.shape.label}\n安全区：${profile.safeArea.label}（点击重新适配）",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    )
                }
            }

            item {
                SectionTitle("主题")
            }
            items(ThemeType.entries) { theme ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { mainViewModel.setTheme(theme) },
                    colors = if (theme == currentTheme) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    } else {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    }
                ) {
                    Text(
                        text = theme.label + if (theme == currentTheme) "（当前）" else "",
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    )
                }
            }

            item {
                SectionTitle("其他")
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenDiagnostics,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "诊断信息 / 崩溃日志",
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 2.dp)
    )
}
