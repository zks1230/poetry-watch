package com.example.poetrywatch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 学习状态枚举 */
enum class StudyStatus {
    /** 未学 */
    NOT_STARTED,
    /** 学习中 */
    LEARNING,
    /** 已掌握 */
    MASTERED
}

/**
 * 学习进度实体
 */
@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val poemId: String,
    val status: StudyStatus = StudyStatus.NOT_STARTED,
    /** 最后复习时间戳 */
    val lastReviewAt: Long? = null,
    /** 累计检测次数 */
    val reviewCount: Int = 0,
    /** 累计答对次数 */
    val correctCount: Int = 0,
    /** 加入背诵计划时间戳 */
    val addedAt: Long = System.currentTimeMillis()
)