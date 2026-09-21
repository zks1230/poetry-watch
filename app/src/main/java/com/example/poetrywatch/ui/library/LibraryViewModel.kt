package com.example.poetrywatch.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: PoetryRepository
) : ViewModel() {

    val poems: StateFlow<List<PoemEntity>> = repository.allPoems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}