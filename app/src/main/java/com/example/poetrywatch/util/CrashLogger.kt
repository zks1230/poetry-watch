package com.example.poetrywatch.util

import android.app.Application
import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 崩溃日志记录器。
 *
 * 之前 App 一崩就只剩系统弹的「已停止运行」，看不到原因。
 * 这里把最后一次崩溃的堆栈写进应用私有目录，下次启动时可以直接在
 * 「设置 → 诊断信息 / 崩溃日志」里看到完整堆栈，方便定位问题。
 */
object CrashLogger {

    private const val FILE_NAME = "crash_last.txt"
    private const val PREFS = "crash_prefs"
    private const val KEY_SEEN_AT = "seen_at"

    fun install(app: Application) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { write(app, thread, throwable) }
            runCatching { previous?.uncaughtException(thread, throwable) }
        }
    }

    private fun write(app: Context, thread: Thread, throwable: Throwable) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val text = buildString {
            appendLine("崩溃时间：$time")
            appendLine("线程：${thread.name}")
            appendLine("---------------- 堆栈 ----------------")
            appendLine(throwable.stackTraceToString())
        }
        runCatching { File(app.filesDir, FILE_NAME).writeText(text) }
    }

    /** 最近一次崩溃信息；没有则返回 null */
    fun lastCrash(context: Context): String? {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return null
        return runCatching { file.readText() }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    /** 是否存在「还没给用户看过」的崩溃，用来决定启动时是否弹出崩溃页 */
    fun hasUnseenCrash(context: Context): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return false
        val seenAt = prefs(context).getLong(KEY_SEEN_AT, 0L)
        return file.lastModified() > seenAt
    }

    fun markSeen(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        runCatching {
            prefs(context).edit().putLong(KEY_SEEN_AT, file.lastModified()).apply()
        }
    }

    fun clear(context: Context) {
        runCatching { File(context.filesDir, FILE_NAME).delete() }
        runCatching { prefs(context).edit().putLong(KEY_SEEN_AT, 0L).apply() }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
