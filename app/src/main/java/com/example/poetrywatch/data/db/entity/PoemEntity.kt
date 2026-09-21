package com.example.poetrywatch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 诗词实体
 */
@Entity(tableName = "poem")
data class PoemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val dynasty: String,
    /** 原文，多句以 \n 分隔 */
    val content: String,
    /** 译文 */
    val translation: String,
    /** 学段：初中 / 高中 */
    val level: String,
    /** 册数，如 "七年级上册" */
    val grade: String,
    /** 教材版本：人教 / 苏教 / 北师大 */
    val version: String = "人教",
    /** 分类：节气/节日/题材等标签，逗号分隔 */
    val category: String = "",
    /** 来源：builtin / remote / user */
    val source: String,
    /** 录入时间戳 */
    val createdAt: Long = System.currentTimeMillis()
)