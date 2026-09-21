package com.example.poetrywatch.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.example.poetrywatch.ui.MainViewModel
import com.example.poetrywatch.ui.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel = hiltViewModel()) {
    val currentTheme by viewModel.theme.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("设置 · 主题") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ThemeType.entries) { theme ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.setTheme(theme) },
                    colors = if (theme == currentTheme) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    } else {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    }
                ) {
                    Text(
                        text = theme.label + if (theme == currentTheme) "（当前）" else "",
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            }
        }
    }
}
