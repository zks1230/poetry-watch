package com.example.poetrywatch.ui.quiz

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val poemTitle by viewModel.poem.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val selected by viewModel.selectedOption.collectAsState()
    val correctSoFar by viewModel.correctSoFar.collectAsState()
    val finished by viewModel.finished.collectAsState()
    val result by viewModel.result.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(poemTitle?.let { "接龙：${it.title}" } ?: "接龙检测") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            if (finished) {
                val r = result ?: QuizResult(0, 0)
                Text(
                    text = "检测完成",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
                Text(
                    text = "答对 ${r.correctCount} / ${r.total}",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("完成") }
                return@Column
            }

            if (questions.isEmpty()) {
                Text(
                    text = "该诗词未生成可检测的句子（需含标点分隔）。",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                )
                return@Column
            }

            val question = questions[currentIndex]
            Text(
                text = "第 ${currentIndex + 1} / ${questions.size} 题",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "\u201C${question.prompt}\u201D ____",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEach { option ->
                    val isCorrect = selected != null && option == question.correct
                    val isWrong = selected == option && option != question.correct
                    OutlinedButton(
                        onClick = { viewModel.selectOption(option) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = when {
                            isCorrect -> androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                            isWrong -> androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                            else -> androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        }
                    ) { Text(option) }
                }
            }

            if (selected != null) {
                Button(
                    onClick = { viewModel.next() },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) { Text(if (currentIndex < questions.size - 1) "下一题" else "查看结果") }
            }
            Text(
                text = "已答对 $correctSoFar 题",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}