package com.example.poetrywatch.ui.quiz

import com.example.poetrywatch.data.db.entity.PoemEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizQuestionFactoryTest {

    private fun poem(
        id: String,
        content: String,
        title: String = "测试"
    ) = PoemEntity(
        id = id,
        title = title,
        author = "佚名",
        dynasty = "唐",
        content = content,
        translation = "译文",
        level = "初中",
        grade = "七年级上册",
        source = "builtin"
    )

    @Test
    fun `带标点的诗生成接龙题且答案在选项里`() {
        val target = poem("builtin-0", "床前明月光，疑是地上霜。\n举头望明月，低头思故乡。")
        val others = listOf(
            poem("builtin-1", "白日依山尽，黄河入海流。\n欲穷千里目，更上一层楼。"),
            poem("builtin-2", "春眠不觉晓，处处闻啼鸟。\n夜来风雨声，花落知多少。")
        )

        val questions = QuizQuestionFactory.build(target, others)

        assertTrue("应该能生成题目", questions.isNotEmpty())
        questions.forEach { q ->
            assertTrue("题面不能为空", q.prompt.isNotBlank())
            assertTrue("答案不能为空", q.correct.isNotBlank())
            assertTrue("选项必须包含正确答案: ${q.options}", q.options.contains(q.correct))
            assertEquals("选项不能重复: ${q.options}", q.options.size, q.options.distinct().size)
            assertTrue("至少要有两个选项", q.options.size >= 2)
        }
    }

    @Test
    fun `没有标点的自录内容也能出题`() {
        val target = poem("user-1", "床前明月光疑是地上霜\n举头望明月低头思故乡")
        val questions = QuizQuestionFactory.build(target, emptyList())

        assertTrue(questions.isNotEmpty())
        questions.forEach { q ->
            assertTrue(q.options.contains(q.correct))
            assertTrue(q.options.size >= 2)
        }
    }

    @Test
    fun `空内容不会抛异常`() {
        val questions = QuizQuestionFactory.build(poem("user-2", ""), emptyList())
        assertTrue(questions.isEmpty())
    }

    @Test
    fun `超短内容不会抛异常`() {
        val questions = QuizQuestionFactory.build(poem("user-3", "月光"), emptyList())
        assertTrue(questions.isEmpty())
    }

    @Test
    fun `单首诗没有任何干扰项时仍有两个选项`() {
        val target = poem("builtin-9", "孤舟蓑笠翁，独钓寒江雪。")
        val questions = QuizQuestionFactory.build(target, listOf(target))

        assertTrue(questions.isNotEmpty())
        questions.forEach { q ->
            assertTrue(q.options.size >= 2)
            assertTrue(q.options.contains(q.correct))
        }
    }

    @Test
    fun `题目数量不超过五道`() {
        val content = (1..12).joinToString("\n") { "第${it}句前半，第${it}句后半。" }
        val questions = QuizQuestionFactory.build(poem("builtin-10", content), emptyList())

        assertTrue(questions.size <= 5)
        assertTrue(questions.isNotEmpty())
    }
}
