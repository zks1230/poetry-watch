package com.example.poetrywatch.data.repository

import com.example.poetrywatch.data.db.dao.PoemDao
import com.example.poetrywatch.data.db.dao.ProgressDao
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoetryRepository @Inject constructor(
    private val poemDao: PoemDao,
    private val progressDao: ProgressDao
) {

    fun allPoems(): Flow<List<PoemEntity>> = poemDao.getAll()

    suspend fun getPoem(id: String): PoemEntity? = poemDao.getById(id)

    suspend fun search(keyword: String): List<PoemEntity> = poemDao.search(keyword)

    suspend fun getPoemCountBuiltin(): Int = poemDao.countBuiltin()

    suspend fun addPoems(poems: List<PoemEntity>) = poemDao.upsertAll(poems)

    suspend fun addPoem(poem: PoemEntity) = poemDao.upsert(poem)

    suspend fun activatePoem(poemId: String) {
        val existing = progressDao.getByPoemId(poemId)
        if (existing == null) {
            progressDao.upsert(
                ProgressEntity(
                    poemId = poemId,
                    status = StudyStatus.LEARNING,
                    addedAt = System.currentTimeMillis()
                )
            )
        }
    }

    /** 手动标记背诵状态 */
    suspend fun updateStatus(poemId: String, status: StudyStatus) {
        val before = progressDao.getByPoemId(poemId)
        progressDao.upsert(
            (before ?: ProgressEntity(poemId = poemId)).copy(
                status = status,
                lastReviewAt = System.currentTimeMillis()
            )
        )
    }

    /** 记录一次检测结果 */
    suspend fun recordResult(poemId: String, correct: Boolean) {
        val before = progressDao.getByPoemId(poemId)
        progressDao.upsert(
            (before ?: ProgressEntity(poemId = poemId)).copy(
                reviewCount = (before?.reviewCount ?: 0) + 1,
                correctCount = (before?.correctCount ?: 0) + if (correct) 1 else 0,
                lastReviewAt = System.currentTimeMillis(),
                status = if (correct) {
                    // 连续答对提升状态
                    if (before?.status == StudyStatus.MASTERED) StudyStatus.MASTERED
                    else StudyStatus.LEARNING
                } else {
                    if (before?.status == StudyStatus.MASTERED) StudyStatus.LEARNING else before?.status ?: StudyStatus.LEARNING
                }
            )
        )
    }

    fun allProgress(): Flow<List<ProgressEntity>> = progressDao.getAll()

    suspend fun getProgress(poemId: String): ProgressEntity? = progressDao.getByPoemId(poemId)

    suspend fun getNotMastered(): List<ProgressEntity> = progressDao.getNotMastered()
}