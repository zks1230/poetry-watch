package com.example.poetrywatch.testing

import com.example.poetrywatch.data.db.dao.PoemDao
import com.example.poetrywatch.data.db.dao.ProgressDao
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** 测试用内存版 PoemDao */
class FakePoemDao(private val poems: List<PoemEntity> = emptyList()) : PoemDao {

    private val store = poems.toMutableList()

    override fun getAll(): Flow<List<PoemEntity>> = flowOf(store.toList())

    override suspend fun getById(id: String): PoemEntity? = store.firstOrNull { it.id == id }

    override suspend fun search(keyword: String): List<PoemEntity> =
        store.filter { it.title.contains(keyword) }

    override suspend fun countBuiltin(): Int = store.count { it.source == "builtin" }

    override suspend fun upsert(poem: PoemEntity) {
        store.removeAll { it.id == poem.id }
        store.add(poem)
    }

    override suspend fun upsertAll(poems: List<PoemEntity>) {
        poems.forEach { upsert(it) }
    }

    override suspend fun clear() = store.clear()
}

/** 测试用内存版 ProgressDao */
class FakeProgressDao : ProgressDao {

    private val store = linkedMapOf<String, ProgressEntity>()

    override fun getAll(): Flow<List<ProgressEntity>> = flowOf(store.values.toList())

    override suspend fun getByPoemId(poemId: String): ProgressEntity? = store[poemId]

    override fun getByStatus(status: StudyStatus): Flow<List<ProgressEntity>> =
        flowOf(store.values.filter { it.status == status })

    override suspend fun getNotMastered(): List<ProgressEntity> =
        store.values.filter { it.status != StudyStatus.MASTERED }

    override suspend fun upsert(progress: ProgressEntity) {
        store[progress.poemId] = progress
    }

    override suspend fun clear() = store.clear()
}

/** 造一首测试诗词 */
fun testPoem(
    id: String = "builtin-0",
    title: String = "静夜思",
    content: String = "床前明月光，疑是地上霜。\n举头望明月，低头思故乡。"
) = PoemEntity(
    id = id,
    title = title,
    author = "李白",
    dynasty = "唐",
    content = content,
    translation = "明亮的月光洒在床前……",
    level = "初中",
    grade = "七年级上册",
    version = "人教",
    category = "",
    source = "builtin"
)
