package com.example.poetrywatch.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.example.poetrywatch.data.db.entity.StudyStatus
import com.example.poetrywatch.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val poem by viewModel.poem.collectAsState()
    val progress by viewModel.progress.collectAsState()
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0 原文 1 译文

    val p = poem
    Scaffold(topBar = { TopAppBar(title = { Text(p?.title ?: "诗词") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            if (p == null) {
                Text("加载中…", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                return@Scaffold
            }

            Text(
                text = "— ${p.dynasty} · ${p.author} —",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = p.grade,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            )

            val statusText = when (progress?.status) {
                StudyStatus.MASTERED -> "已掌握"
                StudyStatus.LEARNING -> "学习中"
                else -> "未学"
            }
            Text(
                text = "背诵状态：$statusText",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Text(
                text = if (mode == 0) "【原文】" else "【译文】",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (mode == 0) p.content else p.translation,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { mode = 1 - mode },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (mode == 0) "查看译文" else "查看原文") }

                if (progress?.status != StudyStatus.MASTERED) {
                    Button(
                        onClick = { navController.navigate(Routes.quiz(p.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("开始检测（接龙）") }
                }

                OutlinedButton(
                    onClick = { viewModel.setStatus(StudyStatus.LEARNING) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("标记为学习中") }

                OutlinedButton(
                    onClick = { viewModel.setStatus(StudyStatus.MASTERED) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("标记为已掌握") }
            }
        }
    }
}