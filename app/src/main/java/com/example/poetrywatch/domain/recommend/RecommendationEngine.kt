package com.example.poetrywatch.domain.recommend

import com.example.poetrywatch.data.db.entity.PoemEntity
import com.example.poetrywatch.data.db.entity.ProgressEntity
import com.example.poetrywatch.data.db.entity.StudyStatus
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * 推荐算法：节气/节日匹配 + 学习进度 双因子加权
 */
class RecommendationEngine {

    companion object {
        /** 节日/节气 → 应景诗词标题 映射 */
        val FESTIVAL_POEMS = mapOf(
            "中秋" to listOf("水调歌头", "十五夜望月", "静夜思"),
            "清明" to listOf("清明"),
            "重阳" to listOf("九月九日忆山东兄弟"),
            "春节" to listOf("元日", "除夜雪"),
            "元宵" to listOf("生查子·元夕"),
            "端午" to listOf("浣溪沙·端午"),
            "春" to listOf("钱塘湖春行", "春望", "江南逢李龟年", "绝句", "惠崇春江晚景", "早春呈水部张十八员外", "次北固山下", "约客"),
            "夏" to listOf("夏日绝句", "约客", "题西林壁"),
            "秋" to listOf("天净沙·秋思", "秋词", "夜雨寄北", "潼关"),
            "冬" to listOf("白雪歌送武判官归京", "夜雪"),
            "月" to listOf("水调歌头", "静夜思", "月夜忆舍弟")
        )

        private const val SEASON_MATCH = 120
        private const val WEEK_MS = 7L * 24 * 3600 * 1000
    }

    /** 根据日期推导应景关键词（节气/节日/季节） */
    fun getFestivalKeyword(date: LocalDate): List<String> {
        val monthDay = date.monthValue * 100 + date.dayOfMonth
        val seasonal = when (date.monthValue) {
            3, 4, 5 -> "春"
            6, 7, 8 -> "夏"
            9, 10, 11 -> "秋"
            else -> "冬"
        }
        val festival = when (monthDay) {
            // 中秋节（农历八月十五，此处按公历近似）
            915 -> "中秋"
            905 -> "清明"       // 清明节在4月初
            101, 102 -> "春节"   // 示例
            else -> null
        }
        return buildList {
            add(seasonal)
            festival?.let { add(it) }
        }
    }

    /**
     * 综合评分选出今日推荐。
     * score = 应景匹配分 + 进度分 + 超期复习加分 + 复习轮次衰减分
     */
    fun recommend(
        poems: List<PoemEntity>,
        progresses: Map<String, ProgressEntity>,
        date: LocalDate,
        excludeIds: Set<String> = emptySet()
    ): PoemEntity? {
        val keywords = getFestivalKeyword(date)
        val now = System.currentTimeMillis()

        return poems
            .filter { it.id !in excludeIds }
            .mapNotNull { poem ->
                val progress = progresses[poem.id]

                // 应景匹配：根据诗词标题/分类是否命中关键词
                val seasonScore = if (keywords.any { kw -> poemCategoryOrTitle(poem).any { it == kw } }) SEASON_MATCH else 0

                val progressScore = when (progress?.status) {
                    null -> 50
                    StudyStatus.NOT_STARTED -> 50
                    StudyStatus.LEARNING -> 30
                    StudyStatus.MASTERED -> 0
                }

                val overdue = progress != null && progress.status != StudyStatus.MASTERED
                        && progress.lastReviewAt != null && now - progress.lastReviewAt > WEEK_MS
                val overdueBonus = if (overdue) 40 else 0

                val decay = progress?.reviewCount?.let { (30.0 / (it + 1)).roundToInt() } ?: 0

                val score = seasonScore + progressScore + overdueBonus + decay
                score to poem
            }
            .maxByOrNull { it.first }
            ?.second
    }

    private fun poemCategoryOrTitle(poem: PoemEntity): List<String> {
        val fromCategory = poem.category.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val fromTitle = FESTIVAL_POEMS.mapNotNull { (kw, titles) ->
            if (titles.any { poem.title.contains(it) }) kw else null
        }
        return fromCategory + fromTitle + listOf(poem.title)
    }
}