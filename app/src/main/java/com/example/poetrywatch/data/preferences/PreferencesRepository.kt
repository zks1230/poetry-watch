package com.example.poetrywatch.data.preferences

import com.example.poetrywatch.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val theme: Flow<ThemeType>
    suspend fun setTheme(theme: ThemeType)

    /** 屏幕适配（形状 + 安全区） */
    val screenProfile: Flow<ScreenProfile>
    suspend fun setScreenProfile(profile: ScreenProfile)

    /** 是否已经完成过首次启动的屏幕适配向导 */
    val setupCompleted: Flow<Boolean>
    suspend fun setSetupCompleted(completed: Boolean)
}
