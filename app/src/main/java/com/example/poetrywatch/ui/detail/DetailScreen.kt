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
import com.example.poetrywatch.data.db.entity.PoemEntity
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
    val loaded by viewModel.loaded.collectAsState()
    val error by viewModel.error.collectAsState()
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0 原文 1 译文

    val p = poem
    val statusText = when (progress?.status) {
        StudyStatus.MASTERED -> "已掌握"
        StudyStatus.LEARNING -> "学习中"
        else -> "未学"
    }
    Scaffold(topBar = { TopAppBar(title = { Text(p?.title ?: "诗词") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            if (p == null) {
                // 不提前 return（见 QuizScreen 注释：提前返回会触发 Compose 运行时崩溃）
                Text(
                    text = when {
                        !loaded -> "加载中…"
                        error != null -> error ?: "读取失败"
                        else -> "没找到这首诗词"
                    },
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) { Text("返回") }
            } else {
                PoemContent(
                    poem = p,
                    statusText = statusText,
                    mode = mode,
                    onToggleMode = { mode = 1 - mode },
                    onStartQuiz = { navController.navigate(Routes.quiz(p.id)) },
                    onMarkLearning = { viewModel.setStatus(StudyStatus.LEARNING) },
                    onMarkMastered = { viewModel.setStatus(StudyStatus.MASTERED) }
                )
            }
        }
    }
}

@Composable
private fun PoemContent(
    poem: PoemEntity,
    statusText: String,
    mode: Int,
    onToggleMode: () -> Unit,
    onStartQuiz: () -> Unit,
    onMarkLearning: () -> Unit,
    onMarkMastered: () -> Unit
) {
    Text(
        text = "— ${poem.dynasty} · ${poem.author} —",
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = poem.grade,
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
    )
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
        text = if (mode == 0) poem.content else poem.translation,
        fontSize = 16.sp,
        lineHeight = 28.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onToggleMode,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (mode == 0) "查看译文" else "查看原文") }

        if (statusText != "已掌握") {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier.fillMaxWidth()
            ) { Text("开始检测（接龙）") }
        }

        OutlinedButton(
            onClick = onMarkLearning,
            modifier = Modifier.fillMaxWidth()
        ) { Text("标记为学习中") }

        OutlinedButton(
            onClick = onMarkMastered,
            modifier = Modifier.fillMaxWidth()
        ) { Text("标记为已掌握") }
    }
}