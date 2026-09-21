package com.example.poetrywatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.preferences.PreferencesRepository
import com.example.poetrywatch.data.seed.SeedDataLoader
import com.example.poetrywatch.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: PreferencesRepository,
    private val seedDataLoader: SeedDataLoader
) : ViewModel() {

    val theme: StateFlow<ThemeType> = preferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeType.MINIMAL)

    init {
        importSeedIfNeeded()
    }

    private fun importSeedIfNeeded() {
        viewModelScope.launch {
            seedDataLoader.importIfNeeded()
        }
    }

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            preferences.setTheme(theme)
        }
    }
}