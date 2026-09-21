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

    private val poemId: String = checkNotNull(savedStateHandle["poemId"])

    private val _poem = MutableStateFlow<PoemEntity?>(null)
    val poem: StateFlow<PoemEntity?> = _poem

    private val _progress = MutableStateFlow<ProgressEntity?>(null)
    val progress: StateFlow<ProgressEntity?> = _progress

    init {
        viewModelScope.launch {
            _poem.value = repository.getPoem(poemId)
        }
        refreshProgress()
    }

    fun refreshProgress() {
        viewModelScope.launch {
            _progress.value = repository.getProgress(poemId)
        }
    }

    fun setStatus(status: StudyStatus) {
        viewModelScope.launch {
            repository.updateStatus(poemId, status)
            refreshProgress()
        }
    }

    fun addToPractice() {
        viewModelScope.launch {
            repository.activatePoem(poemId)
            refreshProgress()
        }
    }
}