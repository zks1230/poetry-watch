package com.example.poetrywatch.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.poetrywatch.ui.Routes

data class NavEntry(val label: String, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val menu = listOf(
        NavEntry("诗词库", Routes.LIBRARY),
        NavEntry("背诵中心", Routes.PRACTICE),
        NavEntry("内容管理", Routes.MANAGE),
        NavEntry("设置", Routes.SETTINGS)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("今日推荐") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                val poem = state.recommended
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("★ 今日推荐", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        if (poem != null) {
                            Text(
                                text = poem.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "— ${poem.author} · ${poem.dynasty} —",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Button(
                                onClick = { navController.navigate(Routes.detail(poem.id)) },
                                modifier = Modifier.padding(top = 12.dp)
                            ) { Text("查看与背诵") }
                        } else {
                            Text(
                                text = "暂无诗词，请先在内容管理中导入。",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "学习中 ${state.learningCount} · 已掌握 ${state.masteredCount} · 共 ${state.poemCount} 首",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(menu, key = { it.route }) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(entry.route) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = entry.label,
                        fontSize = 17.sp,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            }
        }
    }
}