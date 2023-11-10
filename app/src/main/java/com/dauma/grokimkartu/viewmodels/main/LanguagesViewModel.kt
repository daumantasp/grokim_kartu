package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.utils.locale.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LanguagesUiState(
    val selectedLanguage: Language? = null,
    val isCanceled: Boolean = false
)

@HiltViewModel
class LanguagesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LanguagesUiState())
    val uiState: StateFlow<LanguagesUiState> = _uiState.asStateFlow()

    companion object {
        private val TAG = "LanguagesViewModel"
    }

    fun back() = _uiState.update { it.copy(isCanceled = true) }

    fun languageSelected(language: Language) =
        _uiState.update { it.copy(selectedLanguage = language) }
}