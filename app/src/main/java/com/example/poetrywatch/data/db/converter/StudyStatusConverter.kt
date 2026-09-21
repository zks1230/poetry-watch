package com.example.poetrywatch.data.db.converter

import androidx.room.TypeConverter
import com.example.poetrywatch.data.db.entity.StudyStatus

class StudyStatusConverter {
    @TypeConverter
    fun fromString(value: String): StudyStatus = StudyStatus.valueOf(value)

    @TypeConverter
    fun toString(status: StudyStatus): String = status.name
}