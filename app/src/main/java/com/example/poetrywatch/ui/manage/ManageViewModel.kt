package com.example.poetrywatch.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val repository: PoetryRepository
) : ViewModel() {

    fun addUserPoem(
        title: String,
        author: String,
        dynasty: String,
        content: String,
        translation: String,
        grade: String
    ) {
        viewModelScope.launch {
            repository.addPoem(
                PoemEntity(
                    id = "user-${UUID.randomUUID()}",
                    title = title.trim(),
                    author = author.trim(),
                    dynasty = dynasty.trim(),
                    content = content.trim(),
                    translation = translation.trim(),
                    level = "初中",
                    grade = grade.trim().ifEmpty { "自录" },
                    version = "人教",
                    source = "user"
                )
            )
        }
    }

    /** 联网更新诗词库（预留接口，后续接入 API） */
    suspend fun updateFromRemote(): Boolean {
        // TODO: 对接中华诗词库 API，增量下载
        return false
    }
}