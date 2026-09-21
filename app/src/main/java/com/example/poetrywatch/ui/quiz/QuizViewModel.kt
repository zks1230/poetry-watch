package com.example.poetrywatch.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

/** 一道接龙题：给出上句，从 options 选出下句 correct */
data class QuizQuestion(
    val prompt: String,
    val correct: String,
    val options: List<String>
)

data class QuizResult(
    val correctCount: Int,
    val total: Int
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: PoetryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val poemId: String = checkNotNull(savedStateHandle["poemId"])

    private val _poem = MutableStateFlow<PoemEntity?>(null)
    val poem: StateFlow<PoemEntity?> = _poem

    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption

    private val _correctSoFar = MutableStateFlow(0)
    val correctSoFar: StateFlow<Int> = _correctSoFar

    private val _finished = MutableStateFlow(false)
    val finished: StateFlow<Boolean> = _finished

    private val _result = MutableStateFlow<QuizResult?>(null)
    val result: StateFlow<QuizResult?> = _result

    init {
        viewModelScope.launch {
            val target = repository.getPoem(poemId) ?: return@launch
            _poem.value = target
            val others = repository.allPoems().first()
            _questions.value = generateQuestions(target, others)
        }
    }

    fun selectOption(option: String) {
        if (_selectedOption.value != null) return
        _selectedOption.value = option
        val current = _questions.value.getOrNull(_currentIndex.value)
        if (current != null && option == current.correct) {
            _correctSoFar.value += 1
        }
    }

    fun next() {
        val q = _questions.value
        if (_currentIndex.value < q.size - 1) {
            _currentIndex.value += 1
            _selectedOption.value = null
        } else {
            finish()
        }
    }

    private fun finish() {
        val total = _questions.value.size
        val correct = _correctSoFar.value
        _finished.value = true
        _result.value = QuizResult(correct, total)
        viewModelScope.launch {
            // 全部作对视为巩固，否则标记需复习
            repository.recordResult(poemId, correct >= total)
        }
    }

    private suspend fun generateQuestions(target: PoemEntity, others: List<PoemEntity>): List<QuizQuestion> {
        val lines = toCouplets(target)
        if (lines.isEmpty()) return emptyList()

        // 干扰句候选池
        val distractionPool = (others.filter { it.id != target.id }
            .flatMap { toPhraseFragments(it) } + toPhraseFragments(target))
            .distinct()
            .toMutableList()

        return lines.shuffled(Random).take(5).map { (prompt, answer) ->
            val wrong = distractionPool
                .filter { it != answer }
                .shuffled(Random)
                .take(3)
                .ifEmpty {
                    listOf("望明月", "听雨声", "忆故人")
                }
            QuizQuestion(
                prompt = prompt,
                correct = answer,
                options = (wrong + answer).shuffled(Random)
            )
        }
    }

    /** 将诗歌切分为【上句, 下句】对（含标点分隔），返回 list */
    private fun toCouplets(poem: PoemEntity): List<Pair<String, String>> =
        poem.content.lines()
            .mapNotNull { line ->
                val idx = line.indexOfFirst { it in DELIMITERS }
                if (idx > 0 && idx < line.lastIndex) {
                    line.substring(0, idx + 1).trim() to line.substring(idx + 1).trim()
                } else null
            }

    /** 收集诗行中的短语片段作为干扰项 */
    private fun toPhraseFragments(poem: PoemEntity): List<String> =
        poem.content.lines()
            .flatMap { line ->
                line.split(*DELIMITERS.toCharArray())
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && it.length >= 2 }
            }

    companion object {
        private const val DELIMITERS = "，。；？！、"
    }
}