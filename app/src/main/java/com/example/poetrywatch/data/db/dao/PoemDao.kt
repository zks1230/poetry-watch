package com.example.poetrywatch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.poetrywatch.data.db.entity.PoemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemDao {

    @Query("SELECT * FROM poem ORDER BY grade, title")
    fun getAll(): Flow<List<PoemEntity>>

    @Query("SELECT * FROM poem WHERE id = :id")
    suspend fun getById(id: String): PoemEntity?

    @Query("SELECT * FROM poem WHERE title LIKE '%' || :keyword || '%' ORDER BY grade")
    suspend fun search(keyword: String): List<PoemEntity>

    @Query("SELECT COUNT(*) FROM poem WHERE source = 'builtin'")
    suspend fun countBuiltin(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(poem: PoemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(poems: List<PoemEntity>)

    @Query("DELETE FROM poem")
    suspend fun clear()
}