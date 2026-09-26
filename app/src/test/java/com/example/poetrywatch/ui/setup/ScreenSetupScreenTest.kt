package com.example.poetrywatch.ui.setup

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.poetrywatch.data.preferences.SafeArea
import com.example.poetrywatch.data.preferences.ScreenProfile
import com.example.poetrywatch.data.preferences.ScreenShape
import com.example.poetrywatch.ui.theme.ThemeType
import com.example.poetrywatch.ui.theme.UniversalTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * 首次启动「屏幕适配」向导的回归测试：两步都能点，最后能拿到选择结果。
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w400dp-h900dp")
class ScreenSetupScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `两步向导能选出圆形表盘加宽松安全区`() {
        var result: ScreenProfile? = null

        composeRule.setContent {
            UniversalTheme(themeType = ThemeType.MINIMAL) {
                ScreenSetupScreen(
                    initial = ScreenProfile.DEFAULT,
                    onDone = { result = it }
                )
            }
        }

        // 第一步：选形状（用各自独有的说明文字定位，避免「方形表盘 / 长方形表盘」互相包含）
        composeRule.onNodeWithText("屏幕适配 1/2").assertExists()
        composeRule.onNodeWithText("内容收进中心方形区域，四角留白", substring = true)
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("下一步").performScrollTo().performClick()
        composeRule.waitForIdle()

        // 第二步：选安全区
        composeRule.onNodeWithText("屏幕适配 2/2").assertExists()
        composeRule.onNodeWithText("圆屏裁切严重、文字被吃掉时选它", substring = true)
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("开始使用").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertNotNull("点开始使用后应该回调结果", result)
        assertEquals(ScreenShape.ROUND, result?.shape)
        assertEquals(SafeArea.LOOSE, result?.safeArea)
    }

    @Test
    fun `设置里重新适配时能返回上一步`() {
        composeRule.setContent {
            UniversalTheme(themeType = ThemeType.MINIMAL) {
                ScreenSetupScreen(
                    initial = ScreenProfile(ScreenShape.SQUARE, SafeArea.NORMAL),
                    showCancel = true,
                    onDone = {},
                    onCancel = {}
                )
            }
        }

        composeRule.onNodeWithText("内容铺满方形屏幕", substring = true).assertExists()
        composeRule.onNodeWithText("下一步").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("上一步").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("屏幕适配 1/2").assertExists()
        composeRule.onNodeWithText("返回设置").performScrollTo().assertExists()
    }
}
