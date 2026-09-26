package com.example.poetrywatch.data.preferences

/**
 * 屏幕形状。首次启动时由用户选择（类似汉克米应用商店的安全区选择），
 * 这样在系统无法正确上报圆屏标志的华强北手表上也能正确排版。
 */
enum class ScreenShape(val label: String, val hint: String) {
    AUTO("自动识别", "跟随系统（推荐圆屏/方屏正常的设备）"),
    ROUND("圆形表盘", "内容收进中心方形区域，四角留白"),
    SQUARE("方形表盘", "内容铺满方形屏幕"),
    WIDE("长方形表盘", "左右留白，适合横向长条屏幕")
}

/**
 * 安全区（内容向内收缩）档位。数值越大，边缘留白越多，文字越不容易被表壳裁掉。
 */
enum class SafeArea(val label: String, val hint: String, val insetDp: Int) {
    TIGHT("紧凑", "屏幕大、想显示更多内容", 2),
    NORMAL("标准", "大多数手表的推荐值", 10),
    LOOSE("宽松", "圆屏裁切严重、文字被吃掉时选它", 22)
}

/** 用户的屏幕适配结果 */
data class ScreenProfile(
    val shape: ScreenShape = ScreenShape.AUTO,
    val safeArea: SafeArea = SafeArea.NORMAL
) {
    companion object {
        val DEFAULT = ScreenProfile()
    }
}
