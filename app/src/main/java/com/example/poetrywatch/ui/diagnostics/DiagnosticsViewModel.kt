package com.example.poetrywatch.ui.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    repository: PoetryRepository
) : ViewModel() {

    /** (诗词数量, 学习记录数量) */
    val counts: StateFlow<Pair<Int, Int>> = combine(
        repository.allPoems().map { it.size }.catch { emit(0) },
        repository.allProgress().map { it.size }.catch { emit(0) }
    ) { poems, progress -> poems to progress }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0 to 0)
}
