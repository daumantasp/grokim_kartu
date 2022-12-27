package com.dauma.grokimkartu.general.thememodemanager

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.dauma.grokimkartu.general.utils.Utils

class ThemeModeManagerImpl(
    private val utils: Utils
): ThemeModeManager {
    private var themeManager: ThemeManager? = null

    private var _currentThemeMode: ThemeMode = ThemeMode.Light
    override val currentThemeMode: ThemeMode
        get() = _currentThemeMode

    private var _availableThemeModes: MutableList<ThemeMode> = mutableListOf()
    override val availableThemeModes: List<ThemeMode>
        get() = _availableThemeModes

    companion object {
        private const val THEME_MODE_SHARED_PREF_KEY = "THEME_MODE_SHARED_PREF_KEY"
    }

    private fun init() {
        setAvailableThemeModes()
        setCurrentThemeMode()
    }

    private fun setAvailableThemeModes() {
        _availableThemeModes.add(ThemeMode.Light)
        _availableThemeModes.add(ThemeMode.Dark)
        if (isDeviceModeAvailable()) {
            _availableThemeModes.add(ThemeMode.Device)
        }
    }

    private fun isDeviceModeAvailable() : Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    private fun setCurrentThemeMode() {
        val currentThemeModeFromSharedPrefs = loadCurrentThemeModeFromSharedPrefs()
        if (currentThemeModeFromSharedPrefs != null) {
            selectThemeMode(currentThemeModeFromSharedPrefs)
        } else if (isDeviceModeAvailable()) {
            selectThemeMode(ThemeMode.Device)
        } else {
            selectThemeMode(ThemeMode.Light)
        }
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
        init()
    }

    private fun setLight() {
        themeManager?.let {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } ?: throw ThemeModeException(ThemeModeErrors.THEME_MANAGER_IS_NOT_SET)
    }

    private fun setDark() {
        themeManager?.let {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } ?: throw ThemeModeException(ThemeModeErrors.THEME_MANAGER_IS_NOT_SET)
    }

    private fun setDevice() {
        themeManager?.let {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } ?: throw ThemeModeException(ThemeModeErrors.THEME_MANAGER_IS_NOT_SET)
    }

    private fun saveCurrentThemeModeToSharedPrefs() {
        utils.sharedStorageUtils.save(THEME_MODE_SHARED_PREF_KEY, currentThemeMode.toString())
    }

    private fun loadCurrentThemeModeFromSharedPrefs() : ThemeMode? {
        val currentUiModeAsString = utils.sharedStorageUtils.getEntry(THEME_MODE_SHARED_PREF_KEY)
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