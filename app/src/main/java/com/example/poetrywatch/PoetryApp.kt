package com.example.poetrywatch

import android.app.Application
import com.example.poetrywatch.util.CrashLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PoetryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLogger.install(this)
    }
}
