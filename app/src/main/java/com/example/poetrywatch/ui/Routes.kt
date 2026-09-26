package com.example.poetrywatch.ui

/** 导航路由 */
object Routes {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val DETAIL = "detail/{poemId}"
    const val PRACTICE = "practice"
    const val QUIZ = "quiz/{poemId}"
    const val MANAGE = "manage"
    const val SETTINGS = "settings"
    const val SETUP = "setup"
    const val DIAGNOSTICS = "diagnostics"

    fun detail(poemId: String) = "detail/${encode(poemId)}"
    fun quiz(poemId: String) = "quiz/${encode(poemId)}"

    /** 诗词 id 只含字母数字和短横线，这里兜底编码，避免自录内容带特殊字符时导航失败 */
    private fun encode(id: String) = id.replace("/", "%2F").replace("?", "%3F").replace("#", "%23")
}
