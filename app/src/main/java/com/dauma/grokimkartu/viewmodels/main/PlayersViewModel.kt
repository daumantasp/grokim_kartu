package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayersUiState(
    val playersPages: List<PlayersPage> = listOf(),
    val isFilterApplied: Boolean = false,
    val playersFilterStarted: Boolean = false,
    val close: Boolean = false
)

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayersUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "PlayersViewModel"
    }

    init {
        viewModelScope.launch {
            observePlayersPages()
        }
        viewModelScope.launch {
            observeFilterAppliance()
        }
        loadNextPlayersPage()
    }

    fun back() = _uiState.update { it.copy(close = true) }

    fun playersFilter() = _uiState.update { it.copy(playersFilterStarted = true) }

    fun playersFilterStarted() = _uiState.update { it.copy(playersFilterStarted = false) }

    fun loadNextPlayersPage() {
        viewModelScope.launch {
            playersRepository.paginator.loadNextPage()
        }
    }

    fun reload() {
        viewModelScope.launch {
            playersRepository.reload()
        }
    }

    private suspend fun observePlayersPages() {
        playersRepository.paginator.pages.collect { playersPages ->
            _uiState.update { it.copy(playersPages = playersPages) }
        }
    }

    private suspend fun observeFilterAppliance() {
        playersRepository.paginator.isFilterApplied.collect { isFilterApplied ->
            _uiState.update { it.copy(isFilterApplied = isFilterApplied) }
        }
    }
}