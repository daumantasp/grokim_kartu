package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeModesViewModel @Inject constructor(
    private val themeModeManager: ThemeModeManager
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "ThemeModesViewModel"
    }

    fun viewIsReady() {
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }
}