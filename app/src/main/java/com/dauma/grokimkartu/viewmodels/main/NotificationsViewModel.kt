package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notificationsPages: List<NotificationsPage> = listOf(),
    val close: Boolean = false
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "NotificationsViewModelImpl"
    }

    init {
        viewModelScope.launch {
            observeNotificationsPages()
        }
        loadNextNotificationsPage()
    }

    fun back() = _uiState.update { it.copy(close = true) }

    fun notificationExpand(notificationId: Int) {
        viewModelScope.launch {
            notificationsRepository.expand(notificationId)
        }
    }

    fun loadNextNotificationsPage() {
        viewModelScope.launch {
            notificationsRepository.paginator.loadNextPage()
        }
    }

    fun reload() {
        viewModelScope.launch {
            notificationsRepository.reload()
        }
    }

    private suspend fun observeNotificationsPages() {
        notificationsRepository.paginator.pages.collect { notificationsPages ->
            _uiState.update { it.copy(notificationsPages = notificationsPages) }
        }
    }
}