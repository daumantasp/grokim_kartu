package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeModesViewModel @Inject constructor(
    private val themeModeManager: ThemeModeManager,
    private val utils: Utils
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _availableThemeModes = MutableLiveData<Event<List<ThemeMode>>>()
    private val _currentThemeMode = MutableLiveData<Event<ThemeMode>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
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
        _navigation.value = Event(NavigationCommand.Back)
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
        // NOTE: Theme change requires to recreate activity
        // which takes some time. Navigation does not look
        // good if it occurs during theme change
        utils.dispatcherUtils.main.asyncAfterSeconds(0.3) {
            backClicked()
        }
    }

    private fun setCurrentThemeMode() {
        _currentThemeMode.value = Event(themeModeManager.currentThemeMode)
    }

    private fun setAvailableThemeModes() {
        _availableThemeModes.value = Event(themeModeManager.availableThemeModes)
    }
}