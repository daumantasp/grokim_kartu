package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeModesViewModel @Inject constructor(
    private val themeModeManager: ThemeModeManager
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _availableThemeModes = MutableLiveData<Event<List<ThemeMode>>>()
    private val _currentThemeMode = MutableLiveData<Event<ThemeMode>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val availableThemeModes: LiveData<Event<List<ThemeMode>>> = _availableThemeModes
    val currentThemeMode: LiveData<Event<ThemeMode>> = _currentThemeMode

    companion object {
        private val TAG = "ThemeModesViewModel"
    }

    fun viewIsReady() {
        setCurrentThemeMode()
        setAvailableThemeModes()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun lightClicked() {
        themeModeManager.selectThemeMode(ThemeMode.Light)
        setCurrentThemeModeAndNavigateBack()
    }

    fun darkClicked() {
        themeModeManager.selectThemeMode(ThemeMode.Dark)
        setCurrentThemeModeAndNavigateBack()
    }

    fun deviceClicked() {
        themeModeManager.selectThemeMode(ThemeMode.Device)
        setCurrentThemeModeAndNavigateBack()
    }

    private fun setCurrentThemeModeAndNavigateBack() {
        setCurrentThemeMode()
//        backClicked()
    }

    private fun setCurrentThemeMode() {
        _currentThemeMode.value = Event(themeModeManager.currentThemeMode)
    }

    private fun setAvailableThemeModes() {
        _availableThemeModes.value = Event(themeModeManager.availableThemeModes)
    }
}