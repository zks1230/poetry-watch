package com.example.poetrywatch.ui.quiz

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.poetrywatch.data.repository.PoetryRepository
import com.example.poetrywatch.testing.FakePoemDao
import com.example.poetrywatch.testing.FakeProgressDao
import com.example.poetrywatch.testing.testPoem
import com.example.poetrywatch.ui.theme.ThemeType
import com.example.poetrywatch.ui.theme.UniversalTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * 「点击开始检测就停止运行」的回归测试：
 * 在电脑上真实渲染检测页，走完 出题 → 选答案 → 下一题 的流程。
 * 如果这条路径上再出现任何异常，这里会直接失败。
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w400dp-h900dp")
class QuizScreenRenderTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun repository(): PoetryRepository {
        val poems = listOf(
            testPoem(id = "builtin-0", title = "静夜思"),
            testPoem(
                id = "builtin-1",
                title = "登鹳雀楼",
                content = "白日依山尽，黄河入海流。\n欲穷千里目，更上一层楼。"
            ),
            testPoem(
                id = "builtin-2",
                title = "春晓",
                content = "春眠不觉晓，处处闻啼鸟。\n夜来风雨声，花落知多少。"
            )
        )
        return PoetryRepository(FakePoemDao(poems), FakeProgressDao())
    }

    private fun render(viewModel: QuizViewModel) {
        composeRule.setContent {
            UniversalTheme(themeType = ThemeType.MINIMAL) {
                QuizScreen(navController = rememberNavController(), viewModel = viewModel)
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `生成不出题目时显示空状态而不是崩溃`() {
        // 这就是 v1.0 一进检测页必崩的分支：题目为空 → 提前 return
        val repository = PoetryRepository(
            FakePoemDao(listOf(testPoem(id = "builtin-9", content = ""))),
            FakeProgressDao()
        )
        val vm = QuizViewModel(repository, SavedStateHandle(mapOf("poemId" to "builtin-9")))
        render(vm)
        composeRule.waitUntil(timeoutMillis = 5_000) { !vm.loading.value }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("这首诗词暂时生成不了检测题，换一首试试。").assertExists()
        composeRule.onNodeWithText("返回").assertExists()
    }

    @Test
    fun `检测页能渲染出题目`() {
        val vm = QuizViewModel(repository(), SavedStateHandle(mapOf("poemId" to "builtin-0")))
        render(vm)
        composeRule.waitUntil(timeoutMillis = 5_000) { vm.questions.value.isNotEmpty() }
        composeRule.waitForIdle()

        val questions = vm.questions.value
        assertTrue("检测题不该为空", questions.isNotEmpty())
        composeRule.onNodeWithText("第 1 / ${questions.size} 题").assertExists()
    }

    @Test
    fun `能选中答案并进入下一题`() {
        val vm = QuizViewModel(repository(), SavedStateHandle(mapOf("poemId" to "builtin-0")))
        render(vm)
        composeRule.waitUntil(timeoutMillis = 5_000) { vm.questions.value.isNotEmpty() }
        composeRule.waitForIdle()

        val first = vm.questions.value.first()

        // 点正确选项
        composeRule.onNodeWithText(first.correct).performClick()
        composeRule.waitForIdle()
        assertEquals("答对计数应 +1", 1, vm.correctSoFar.value)
        composeRule.onNodeWithText("下一题").assertExists()

        // 走到下一题
        composeRule.onNodeWithText("下一题").performClick()
        composeRule.waitForIdle()
        assertEquals(1, vm.currentIndex.value)
        composeRule.onNodeWithText("第 2 / ${vm.questions.value.size} 题").assertExists()
    }

    @Test
    fun `诗词编号缺失时显示错误而不是崩溃`() {
        val vm = QuizViewModel(repository(), SavedStateHandle())
        render(vm)
        composeRule.waitUntil(timeoutMillis = 5_000) { vm.error.value != null }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("重试").assertExists()
        composeRule.onNodeWithText("返回").assertExists()
    }

    @Test
    fun `诗词不存在时显示错误而不是崩溃`() {
        val vm = QuizViewModel(repository(), SavedStateHandle(mapOf("poemId" to "不存在的编号")))
        render(vm)
        composeRule.waitUntil(timeoutMillis = 5_000) { vm.error.value != null }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("重试").assertExists()
    }
}
