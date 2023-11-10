package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ThemeModesUiState(
    val selectedThemeMode: ThemeMode? = null,
    val isCanceled: Boolean = false
)

@HiltViewModel
class ThemeModesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeModesUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "ThemeModesViewModel"
    }

    fun back() = _uiState.update { it.copy(isCanceled = true) }

    fun themeModeSelected(themeMode: ThemeMode) =
        _uiState.update { it.copy(selectedThemeMode = themeMode) }
}