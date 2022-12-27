package com.dauma.grokimkartu.general.thememodemanager

import androidx.appcompat.app.AppCompatDelegate
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils

class ThemeModeManagerImpl(
    private val utils: Utils
): ThemeModeManager {
    private var themeManager: ThemeManager? = null

    private var _currentThemeMode: ThemeMode
    override val currentThemeMode: ThemeMode
        get() = _currentThemeMode

    private var _availableThemeModes: MutableList<ThemeMode> = mutableListOf()
    override val availableThemeModes: List<ThemeMode>
        get() = _availableThemeModes

    companion object {
        private val DEFAULT_THEME_MODE = ThemeMode.Light
        private const val UI_MODE_SHARED_PREF_KEY = "UI_MODE_SHARED_PREF_KEY"
    }

    init {
        _currentThemeMode = loadCurrentThemeModeFromSharedPrefs() ?: DEFAULT_THEME_MODE
        // DEVELOPMENT
        _availableThemeModes = mutableListOf(ThemeMode.Light, ThemeMode.Dark)
    }

    override fun selectThemeMode(themeMode: ThemeMode) {
        if (themeMode != _currentThemeMode && _availableThemeModes.contains(themeMode)) {
            when (themeMode) {
                ThemeMode.Light -> setLight()
                ThemeMode.Dark -> setDark()
                ThemeMode.Device -> setDevice()
            }
            _currentThemeMode = themeMode
            saveCurrentThemeModeToSharedPrefs()
        }
    }

    override fun with(themeManager: ThemeManager) {
        this.themeManager = themeManager
    }

    private fun setLight() {
        themeManager?.let {
            it.setTheme(R.style.LightTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setDark() {
        themeManager?.let {
            it.setTheme(R.style.DarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setDevice() {
        // TODO
    }

    private fun saveCurrentThemeModeToSharedPrefs() {
        utils.sharedStorageUtils.save(UI_MODE_SHARED_PREF_KEY, currentThemeMode.toString())
    }

    private fun loadCurrentThemeModeFromSharedPrefs() : ThemeMode? {
        val currentUiModeAsString = utils.sharedStorageUtils.getEntry(UI_MODE_SHARED_PREF_KEY)
        currentUiModeAsString?.let {
            return when (it) {
                ThemeMode.Light.toString() -> ThemeMode.Light
                ThemeMode.Dark.toString() -> ThemeMode.Dark
                ThemeMode.Device.toString() -> ThemeMode.Device
                else -> null
            }
        }
        return null
    }
}