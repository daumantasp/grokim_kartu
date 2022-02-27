package com.dauma.grokimkartu.ui

interface StatusBarManager {
    fun changeStatusBarTheme(theme: StatusBarTheme)
}

enum class StatusBarTheme {
    LOGIN,
    MAIN
}