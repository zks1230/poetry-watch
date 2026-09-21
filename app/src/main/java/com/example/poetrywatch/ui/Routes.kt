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

    fun detail(poemId: String) = "detail/$poemId"
    fun quiz(poemId: String) = "quiz/$poemId"
}