package com.example.poetrywatch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM progress")
    fun getAll(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE poemId = :poemId")
    suspend fun getByPoemId(poemId: String): ProgressEntity?

    @Query("SELECT * FROM progress WHERE status = :status")
    fun getByStatus(status: StudyStatus): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE status != 'MASTERED'")
    suspend fun getNotMastered(): List<ProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity)

    @Query("DELETE FROM progress")
    suspend fun clear()
}