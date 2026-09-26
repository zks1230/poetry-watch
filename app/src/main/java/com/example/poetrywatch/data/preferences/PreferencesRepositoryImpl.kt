package com.example.poetrywatch.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        val SCREEN_SHAPE = stringPreferencesKey("screen_shape")
        val SAFE_AREA = stringPreferencesKey("safe_area")
        val SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
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

    override val screenProfile: Flow<ScreenProfile> = context.dataStore.data
        .map { prefs ->
            val shape = runCatching { ScreenShape.valueOf(prefs[Keys.SCREEN_SHAPE] ?: "") }
                .getOrDefault(ScreenShape.AUTO)
            val safeArea = runCatching { SafeArea.valueOf(prefs[Keys.SAFE_AREA] ?: "") }
                .getOrDefault(SafeArea.NORMAL)
            ScreenProfile(shape = shape, safeArea = safeArea)
        }

    override suspend fun setScreenProfile(profile: ScreenProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCREEN_SHAPE] = profile.shape.name
            prefs[Keys.SAFE_AREA] = profile.safeArea.name
        }
    }

    override val setupCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[Keys.SETUP_COMPLETED] ?: false }

    override suspend fun setSetupCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SETUP_COMPLETED] = completed
        }
    }
}
