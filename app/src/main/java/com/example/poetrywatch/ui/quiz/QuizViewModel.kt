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

    /** 取不到 id 也不要崩，走错误提示分支 */
    private val poemId: String = savedStateHandle.get<String>("poemId").orEmpty()

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

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        load()
    }

    /**
     * 任何异常都收敛成界面上的错误提示，绝不让 App 直接崩掉。
     */
    fun load() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            runCatching {
                check(poemId.isNotBlank()) { "没有拿到诗词编号" }
                val target = repository.getPoem(poemId)
                    ?: throw IllegalStateException("诗词不存在或已被删除")
                _poem.value = target
                val others = runCatching { repository.allPoems().first() }.getOrDefault(emptyList())
                _questions.value = QuizQuestionFactory.build(target, others)
            }.onFailure { throwable ->
                _error.value = buildString {
                    append("检测题生成失败：")
                    append(throwable.javaClass.simpleName)
                    throwable.message?.let { append(" · ").append(it) }
                }
            }
            _loading.value = false
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
            runCatching { repository.recordResult(poemId, correct >= total) }
        }
    }
}
