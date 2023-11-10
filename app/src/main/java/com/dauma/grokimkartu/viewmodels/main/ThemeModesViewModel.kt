package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeModesViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loaded)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        data object Loaded : UiState()
        data class ThemeModeSelected(val themeMode: ThemeMode) : UiState()
        data object Canceled : UiState()
    }

    companion object {
        private val TAG = "ThemeModesViewModel"
    }

    fun back() {
        _uiState.value = UiState.Canceled
    }

    fun themeModeSelected(themeMode: ThemeMode) {
        _uiState.value = UiState.ThemeModeSelected(themeMode)
    }
}