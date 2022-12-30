package com.dauma.grokimkartu.general.thememodemanager

interface ThemeModeManager {
    val currentThemeMode: ThemeMode
    val currentTheme: Theme
    val availableThemeModes: List<ThemeMode>
    fun selectThemeMode(themeMode: ThemeMode)
    fun with(themeManager: ThemeManager)
}