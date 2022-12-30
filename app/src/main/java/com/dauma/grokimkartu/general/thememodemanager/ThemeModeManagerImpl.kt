package com.dauma.grokimkartu.general.thememodemanager

import android.content.res.Configuration
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

    override val currentTheme: Theme
        get() = getTheme()

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

    // NOTE: Sometimes bug occurs but I didn't find all the reasons why this is happening.
    // If app and OS have opposite theme settings, then only MainActivity is colorized according
    // to the app settings, while all the fragments gets theme according to OS setting.
    // I think this is related with the fact that theme mode is selected before onCreate
    // in MainActivity and thus this method (selectThemeMode) is called before fragments creation.
    // But I don't understand why this bug only occurs sometimes.
    override fun selectThemeMode(themeMode: ThemeMode) {
        if (_availableThemeModes.contains(themeMode)) {
            when (themeMode) {
                ThemeMode.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                ThemeMode.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                ThemeMode.Device -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            _currentThemeMode = themeMode
            saveCurrentThemeModeToSharedPrefs()
        } else {
            throw ThemeModeException(ThemeModeErrors.THEME_MODE_IS_NOT_AVAILABLE)
        }
    }

    override fun with(themeManager: ThemeManager) {
        this.themeManager = themeManager
        init()
    }

    // https://stackoverflow.com/questions/41391404/how-to-get-appcompatdelegate-current-mode-if-default-is-auto
    private fun getTheme() : Theme {
        themeManager?.let {
            val currentNightMode = it.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> Theme.Light // Night mode is not active, we're using the light theme
                Configuration.UI_MODE_NIGHT_YES -> Theme.Dark // Night mode is active, we're using dark theme
                else -> throw ThemeModeException(ThemeModeErrors.UNKNOWN)
            }
        }?: throw ThemeModeException(ThemeModeErrors.THEME_MANAGER_IS_NOT_SET)
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