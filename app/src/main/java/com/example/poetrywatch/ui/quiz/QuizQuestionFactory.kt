package com.example.poetrywatch.ui.quiz

import com.example.poetrywatch.data.db.entity.PoemEntity
import kotlin.random.Random

/**
 * 接龙检测题生成器（纯逻辑，方便单元测试）。
 *
 * 之所以从 ViewModel 里抽出来：之前「点击开始检测就闪退」，
 * 这类逻辑必须能在电脑上直接跑测试验证，而不是只能装到表上试。
 */
object QuizQuestionFactory {

    private const val DELIMITERS = "，。；？！、"

    /** 固定种子，保证同一首诗生成的题序稳定，方便排查问题 */
    private val RANDOM = Random(20260926)

    fun build(target: PoemEntity, others: List<PoemEntity>): List<QuizQuestion> {
        val lines = couplets(target).ifEmpty { halfLines(target) }
        if (lines.isEmpty()) return emptyList()

        val pool = (others.filter { it.id != target.id }.flatMap { fragments(it) } + fragments(target))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        return lines.distinct().shuffled(RANDOM).take(MAX_QUESTIONS).map { (prompt, answer) ->
            val wrong = pool
                .filter { it != answer && it != prompt && it.length <= answer.length + 3 }
                .shuffled(RANDOM)
                .take(3)
            val options = (wrong + answer).distinct()
            QuizQuestion(
                prompt = prompt,
                correct = answer,
                options = if (options.size >= 2) options.shuffled(RANDOM) else listOf(answer, prompt)
            )
        }
    }

    /** 按标点切成【上句, 下句】 */
    private fun couplets(poem: PoemEntity): List<Pair<String, String>> =
        poem.content.lines().mapNotNull { line ->
            val idx = line.indexOfFirst { it in DELIMITERS }
            if (idx > 0 && idx < line.lastIndex) {
                line.substring(0, idx + 1).trim() to line.substring(idx + 1).trim()
            } else null
        }.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }

    /** 没有标点时兜底：长句从中间切开 */
    private fun halfLines(poem: PoemEntity): List<Pair<String, String>> =
        poem.content.lines().mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.length < 4) return@mapNotNull null
            val half = trimmed.length / 2
            trimmed.substring(0, half) to trimmed.substring(half)
        }

    /** 诗行里的短语片段，用作干扰项 */
    private fun fragments(poem: PoemEntity): List<String> =
        poem.content.lines().flatMap { line ->
            line.split(*DELIMITERS.toCharArray())
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.length >= 2 }
        }

    private const val MAX_QUESTIONS = 5
}
