package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrivateConversationsUiState(
    val conversations: List<Conversation> = listOf()
)

@HiltViewModel
class PrivateConversationsViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivateConversationsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "PrivateConversationsViewModelImpl"
    }

    init {
        loadConversationsAndUnreadCount()
    }

    fun reload() = loadConversationsAndUnreadCount()

    private fun loadConversationsAndUnreadCount() {
        viewModelScope.launch {
            loadConversations()
        }
        viewModelScope.launch {
            profileRepository.reloadUnreadCount()
        }
    }

    private suspend fun loadConversations() {
        val conversations = privateConversationsRepository.conversations()
        _uiState.update { it.copy(conversations = conversations.data ?: listOf()) }
    }
}