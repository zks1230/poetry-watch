package com.example.poetrywatch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.poetrywatch.data.db.converter.StudyStatusConverter
import com.example.poetrywatch.data.db.dao.PoemDao
import com.example.poetrywatch.data.db.dao.ProgressDao
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Database(
    entities = [PoemEntity::class, ProgressEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StudyStatusConverter::class)
abstract class PoetryDatabase : RoomDatabase() {
    abstract fun poemDao(): PoemDao
    abstract fun progressDao(): ProgressDao

    companion object {
        const val NAME = "poetry.db"
        const val SEED_FILE = "seed_poems.json"
    }
}

/** 工厂 + 自动 seed：数据库首次创建时从 assets 导入内置诗词 */
@Singleton
class PoetryDatabaseFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val db: PoetryDatabase by lazy { build() }

    private fun build(): PoetryDatabase {
        val db = Room.databaseBuilder(
            context,
            PoetryDatabase::class.java,
            PoetryDatabase.NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        // 首次创建后 seed
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch { seedIfEmpty(db) }
        return db
    }

    private suspend fun seedIfEmpty(db: PoetryDatabase) = withContext(Dispatchers.IO) {
        val dao = db.poemDao()
        if (runCatching { dao.countBuiltin() }.getOrDefault(0) > 0) return@withContext

        val text = runCatching {
            context.assets.open(PoetryDatabase.SEED_FILE).bufferedReader().use { it.readText() }
        }.getOrNull() ?: return@withContext

        val type = object : TypeToken<List<SeedPoem>>() {}.type
        val seeds = Gson().fromJson<List<SeedPoem>>(text, type) ?: return@withContext
        if (seeds.isEmpty()) return@withContext

        dao.upsertAll(seeds.mapIndexed { i, s ->
            PoemEntity(
                id = "builtin-$i",
                title = s.title,
                author = s.author,
                dynasty = s.dynasty,
                content = s.content,
                translation = s.translation,
                level = "初中",
                grade = s.grade,
                version = "人教",
                category = s.category,
                source = "builtin"
            )
        })
    }
}

private data class SeedPoem(
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,
    val translation: String,
    val grade: String,
    val category: String = ""
)

