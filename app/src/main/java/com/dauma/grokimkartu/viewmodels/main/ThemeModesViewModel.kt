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
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val availableThemeModes: LiveData<Event<List<ThemeMode>>> = _availableThemeModes

    companion object {
        private val TAG = "ThemeModesViewModel"
    }

    fun viewIsReady() {
        setAvailableThemeModes()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    private fun setAvailableThemeModes() {
        _availableThemeModes.value = Event(themeModeManager.availableThemeModes)
    }
}