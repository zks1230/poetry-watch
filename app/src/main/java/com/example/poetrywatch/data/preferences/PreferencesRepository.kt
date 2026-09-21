package com.example.poetrywatch.data.preferences

import com.example.poetrywatch.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val theme: Flow<ThemeType>
    suspend fun setTheme(theme: ThemeType)
}