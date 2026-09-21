package com.example.poetrywatch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import com.example.poetrywatch.data.repository.PoetryRepository
import com.example.poetrywatch.domain.recommend.RecommendationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PoetryRepository,
    private val engine: RecommendationEngine
) : ViewModel() {

    private val progressMap: StateFlow<Map<String, ProgressEntity>> = repository.allProgress()
        .map { list -> list.associateBy { it.poemId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val uiState: StateFlow<HomeUiState> = combine(
        repository.allPoems(),
        progressMap
    ) { poems, progs ->
        val recommended = engine.recommend(poems, progs, LocalDate.now())
        HomeUiState(
            recommended = recommended,
            poemCount = poems.size,
            learningCount = progs.values.count { it.status == StudyStatus.LEARNING },
            masteredCount = progs.values.count { it.status == StudyStatus.MASTERED }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun addToPractice(poemId: String) {
        viewModelScope.launch { repository.activatePoem(poemId) }
    }
}

data class HomeUiState(
    val recommended: PoemEntity? = null,
    val poemCount: Int = 0,
    val learningCount: Int = 0,
    val masteredCount: Int = 0
)