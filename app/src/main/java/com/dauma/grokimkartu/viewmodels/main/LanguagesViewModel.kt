package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.utils.locale.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LanguagesViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loaded)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        data object Loaded : UiState()
        data class LanguageSelected(val language: Language) : UiState()
        data object Canceled : UiState()
    }

    companion object {
        private val TAG = "LanguagesViewModel"
    }

    fun back() {
        _uiState.value = UiState.Canceled
    }

    fun languageSelected(language: Language) {
        _uiState.value = UiState.LanguageSelected(language)
    }
}