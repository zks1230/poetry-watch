package com.example.poetrywatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.preferences.PreferencesRepository
import com.example.poetrywatch.data.preferences.ScreenProfile
import com.example.poetrywatch.data.seed.SeedDataLoader
import com.example.poetrywatch.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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

    /** 屏幕适配方案（形状 + 安全区） */
    val screenProfile: StateFlow<ScreenProfile> = preferences.screenProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenProfile.DEFAULT)

    /** null = 还没读到（避免启动瞬间闪一下向导） */
    val setupCompleted: StateFlow<Boolean?> = preferences.setupCompleted
        .map { value -> value }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        importSeedIfNeeded()
    }

    private fun importSeedIfNeeded() {
        viewModelScope.launch {
            runCatching { seedDataLoader.importIfNeeded() }
        }
    }

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            runCatching { preferences.setTheme(theme) }
        }
    }

    /** 保存屏幕适配并标记向导已完成 */
    fun saveScreenProfile(profile: ScreenProfile) {
        viewModelScope.launch {
            runCatching {
                preferences.setScreenProfile(profile)
                preferences.setSetupCompleted(true)
            }
        }
    }

    /** 只保存不关闭向导（预览时用） */
    fun applyScreenProfile(profile: ScreenProfile) {
        viewModelScope.launch {
            runCatching { preferences.setScreenProfile(profile) }
        }
    }

    /** 重新打开屏幕适配向导 */
    fun reopenSetup() {
        viewModelScope.launch {
            runCatching { preferences.setSetupCompleted(false) }
        }
    }
}
