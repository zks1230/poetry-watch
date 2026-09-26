package com.example.poetrywatch.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PoetryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /** 取不到 id 也不再抛异常导致闪退 */
    private val poemId: String = savedStateHandle.get<String>("poemId").orEmpty()

    private val _poem = MutableStateFlow<PoemEntity?>(null)
    val poem: StateFlow<PoemEntity?> = _poem

    private val _progress = MutableStateFlow<ProgressEntity?>(null)
    val progress: StateFlow<ProgressEntity?> = _progress

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            runCatching {
                check(poemId.isNotBlank()) { "没有拿到诗词编号" }
                _poem.value = repository.getPoem(poemId)
            }.onFailure { throwable ->
                _error.value = "读取诗词失败：${throwable.javaClass.simpleName}"
            }
            refreshProgress()
            _loaded.value = true
        }
    }

    fun refreshProgress() {
        viewModelScope.launch {
            runCatching { _progress.value = repository.getProgress(poemId) }
        }
    }

    fun setStatus(status: StudyStatus) {
        viewModelScope.launch {
            runCatching {
                repository.updateStatus(poemId, status)
                _progress.value = repository.getProgress(poemId)
            }
        }
    }

    fun addToPractice() {
        viewModelScope.launch {
            runCatching {
                repository.activatePoem(poemId)
                _progress.value = repository.getProgress(poemId)
            }
        }
    }
}
