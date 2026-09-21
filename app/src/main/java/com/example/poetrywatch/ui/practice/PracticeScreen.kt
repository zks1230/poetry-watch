package com.example.poetrywatch.ui.practice

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.example.poetrywatch.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    navController: NavController,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf("未学", "学习中", "已掌握")
    val currentList = when (tab) {
        0 -> state.notStarted
        1 -> state.learning
        else -> state.mastered
    }

    Scaffold(topBar = { TopAppBar(title = { Text("背诵中心") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            RowChips(tabs = tabs, selected = tab, onSelect = { tab = it })

            if (currentList.isEmpty()) {
                Text(
                    text = "该分类暂无诗词",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(currentList, key = { it.id }) { poem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate(Routes.detail(poem.id)) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "《${poem.title}》 ${poem.author}",
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowChips(tabs: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            FilterChip(
                selected = selected == index,
                onClick = { onSelect(index) },
                label = { Text(label, modifier = Modifier.padding(horizontal = 4.dp)) }
            )
        }
    }
}
