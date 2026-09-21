package com.example.poetrywatch.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val repository: PoetryRepository
) : ViewModel() {

    private val progressMap: StateFlow<Map<String, ProgressEntity>> = repository.allProgress()
        .map { list -> list.associateBy { it.poemId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val poems: StateFlow<List<PoemEntity>> = repository.allPoems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<PracticeUiState> = combine(poems, progressMap) { p, pr ->
        PracticeUiState(
            notStarted = p.filter { (pr[it.id]?.status ?: StudyStatus.NOT_STARTED) == StudyStatus.NOT_STARTED },
            learning = p.filter { (pr[it.id]?.status ?: StudyStatus.NOT_STARTED) == StudyStatus.LEARNING },
            mastered = p.filter { (pr[it.id]?.status ?: StudyStatus.NOT_STARTED) == StudyStatus.MASTERED }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PracticeUiState())

    fun addToPractice(poemId: String) {
        viewModelScope.launch { repository.activatePoem(poemId) }
    }
}

data class PracticeUiState(
    val notStarted: List<PoemEntity> = emptyList(),
    val learning: List<PoemEntity> = emptyList(),
    val mastered: List<PoemEntity> = emptyList()
)