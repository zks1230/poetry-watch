package com.example.poetrywatch.data.seed

import android.content.Context
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.repository.PoetryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** seed JSON 实体 */
data class SeedPoem(
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,
    val translation: String,
    val grade: String,
    val category: String = ""
)

@Singleton
class SeedDataLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PoetryRepository
) {
    private val gson = Gson()

    /** 首次启动时导入内置诗词；若已导入或数据缺失则跳过 */
    suspend fun importIfNeeded() {
        val existing = runCatching { repository.getPoemCountBuiltin() }.getOrDefault(1)
        if (existing > 0) return
        val poems = runCatching { loadFromAssets() }.getOrNull() ?: return
        if (poems.isEmpty()) return

        repository.addPoems(
            poems.mapIndexed { index, seed ->
                PoemEntity(
                    id = "builtin-$index",
                    title = seed.title,
                    author = seed.author,
                    dynasty = seed.dynasty,
                    content = seed.content,
                    translation = seed.translation,
                    level = "初中",
                    grade = seed.grade,
                    version = "人教",
                    category = seed.category,
                    source = "builtin"
                )
            }
        )
    }

    private suspend fun loadFromAssets(): List<SeedPoem> = withContext(Dispatchers.IO) {
        val text = context.assets.open(SEED_FILE).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<SeedPoem>>() {}.type
        gson.fromJson<List<SeedPoem>>(text, type) ?: emptyList()
    }

    companion object {
        const val SEED_FILE = "seed_poems.json"
    }
}