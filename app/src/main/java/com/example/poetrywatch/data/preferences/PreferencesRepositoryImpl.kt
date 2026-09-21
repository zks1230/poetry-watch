package com.example.poetrywatch.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.poetrywatch.ui.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
    }

    override val theme: Flow<ThemeType> = context.dataStore.data
        .map { prefs ->
            val name = prefs[Keys.THEME] ?: ThemeType.MINIMAL.name
            runCatching { ThemeType.valueOf(name) }.getOrDefault(ThemeType.MINIMAL)
        }

    override suspend fun setTheme(theme: ThemeType) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme.name
        }
    }
}