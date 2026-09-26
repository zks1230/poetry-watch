package com.example.poetrywatch.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

/**
 * 接龙检测页。
 *
 * 注意：这里刻意**不使用 `return@Column` 提前返回**。
 * v1.0 的写法是「先 if(finished) { ...; return@Column }」，
 * 结果一进检测页就触发 Compose 运行时的
 * `ArrayIndexOutOfBoundsException: Index -5 out of bounds for length 0`（整个 App 直接停止运行）。
 * 现在改成 when/else 分支结构，每个分支都有明确的结束，避免这类崩溃。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val poem by viewModel.poem.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val selected by viewModel.selectedOption.collectAsState()
    val correctSoFar by viewModel.correctSoFar.collectAsState()
    val finished by viewModel.finished.collectAsState()
    val result by viewModel.result.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val back: () -> Unit = { navController.popBackStack() }

    Scaffold(
        topBar = { TopAppBar(title = { Text(poem?.let { "接龙：${it.title}" } ?: "接龙检测") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val errorText = error
            when {
                // 出错也不再闪退，直接把原因显示出来
                errorText != null -> ErrorState(
                    message = errorText,
                    onRetry = { viewModel.load() },
                    onBack = back
                )

                loading -> HintState(text = "正在生成检测题…")

                finished -> ResultState(
                    result = result ?: QuizResult(0, 0),
                    onFinish = back
                )

                questions.isEmpty() -> EmptyState(onBack = back)

                else -> QuestionState(
                    questions = questions,
                    currentIndex = currentIndex,
                    selected = selected,
                    correctSoFar = correctSoFar,
                    onSelect = { viewModel.selectOption(it) },
                    onNext = { viewModel.next() }
                )
            }
        }
    }
}

@Composable
private fun HintState(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
        )
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) { Text("重试") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("返回") }
    }
}

@Composable
private fun EmptyState(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "这首诗词暂时生成不了检测题，换一首试试。",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        )
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("返回") }
    }
}

@Composable
private fun ResultState(
    result: QuizResult,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "检测完成",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        )
        Text(
            text = "答对 ${result.correctCount} / ${result.total}",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("完成") }
    }
}

@Composable
private fun QuestionState(
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selected: String?,
    correctSoFar: Int,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val index = currentIndex.coerceIn(0, questions.lastIndex)
    val question = questions[index]

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "第 ${index + 1} / ${questions.size} 题",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "\u201C${question.prompt}\u201D ____",
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            question.options.forEach { option ->
                val isCorrect = selected != null && option == question.correct
                val isWrong = selected == option && option != question.correct
                OutlinedButton(
                    onClick = { onSelect(option) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = when {
                        isCorrect -> ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                        isWrong -> ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                        else -> ButtonDefaults.outlinedButtonColors()
                    }
                ) { Text(option, fontSize = 14.sp) }
            }
        }

        if (selected != null) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(if (index < questions.size - 1) "下一题" else "查看结果")
            }
        }
        Text(
            text = "已答对 $correctSoFar 题",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}
