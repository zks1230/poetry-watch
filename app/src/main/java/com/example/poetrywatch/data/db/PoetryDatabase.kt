package com.example.poetrywatch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.poetrywatch.data.db.converter.StudyStatusConverter
import com.example.poetrywatch.data.db.dao.PoemDao
import com.example.poetrywatch.data.db.dao.ProgressDao
import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity

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
    }
}