package com.example.poetrywatch.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val poems by viewModel.poems.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("诗词库 · 初中人教版") }) }) { padding ->
        val grouped = poems.groupBy { it.grade }.toSortedMap()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (poems.isEmpty()) {
                item { Text("内置库正在初始化…", color = MaterialTheme.colorScheme.secondary) }
            }
            grouped.forEach { (grade, list) ->
                item(key = "h-$grade") {
                    Text(
                        text = grade,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                list.forEach { poem ->
                    item(key = poem.id) {
                        PoemCard(poem, onClick = { navController.navigate(Routes.detail(poem.id)) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PoemCard(poem: PoemEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "《${poem.title}》 ${poem.author}",
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}