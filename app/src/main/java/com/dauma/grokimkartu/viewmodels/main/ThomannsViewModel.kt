package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThomannsUiState(
    val isAllTabActive: Boolean = true,
    val isFilterStarted: Boolean = false,
    val isCreateStarted: Boolean = false,
    val isFilterApplied: Boolean = false,
    val close: Boolean = false
)

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ThomannsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeFilterAppliance()
        }
    }

    fun back() = _uiState.update { it.copy(close = true) }

    fun tabSelected(isAllTabActive: Boolean) = _uiState.update { it.copy(
        isAllTabActive = isAllTabActive,
        isFilterStarted = false,
        isCreateStarted = false
    ) }

    fun filterClicked() {
        if (_uiState.value.isAllTabActive)
            _uiState.update { it.copy(isFilterStarted = true, isCreateStarted = false) }
    }

    fun createClicked() {
        _uiState.update { it.copy(isFilterStarted = false, isCreateStarted = true) }
    }

    private suspend fun observeFilterAppliance() {
        thomannsRepository.paginator.isFilterApplied.collect { isFilterApplied ->
            _uiState.update { it.copy(isFilterApplied = isFilterApplied) }
        }
    }
}