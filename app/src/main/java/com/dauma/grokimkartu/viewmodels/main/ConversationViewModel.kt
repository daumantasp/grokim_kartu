package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationUiState(
    val title: String = "",
    val conversationPages: List<ConversationPage> = listOf(),
    val close: Boolean = false
)

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository,
    private val thomannConversationsRepository: ThomannConversationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userNameSaved = savedStateHandle.get<String>("userName")
    private val userId = savedStateHandle.get<Int>("userId")
    private val thomannId = savedStateHandle.get<Int>("thomannId")
    private val isPrivate: Boolean
        get() = userId != -1

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "ConversationViewModel"
    }

    init {
        viewModelScope.launch {
            observeConversationPages()
        }
        setId()
        setTitle()
        loadNextConversationPage()
    }

    override fun onCleared() {
        super.onCleared()
        privateConversationsRepository.paginator.setConversationPartnerId(null)
        thomannConversationsRepository.paginator.setThomannId(null)
    }

    fun back() = _uiState.update { it.copy(close = true) }

    fun loadNextConversationPage() {
        viewModelScope.launch {
            if (isPrivate)
                privateConversationsRepository.paginator.loadNextPage()
            else
                thomannConversationsRepository.paginator.loadNextPage()
        }
    }

    fun postMessageClicked(messageText: String) {
        if (messageText.isBlank())
            return

        viewModelScope.launch {
            if (isPrivate)
                privateConversationsRepository.postMessage(PostMessage(messageText))
            else
                thomannConversationsRepository.postMessage(PostMessage(messageText))
        }
    }

    private fun setId() {
        if (isPrivate)
            privateConversationsRepository.paginator.setConversationPartnerId(userId)
        else
            thomannConversationsRepository.paginator.setThomannId(thomannId)
    }

    private fun setTitle() {
        val title = if (isPrivate) (userNameSaved ?: "#$userId") else "#$thomannId"
        _uiState.update { it.copy(title = title) }
    }

    private suspend fun observeConversationPages() {
        if (isPrivate)
            privateConversationsRepository.paginator.pages.collect { conversationPages ->
                _uiState.update { it.copy(conversationPages = conversationPages) }
            }
        else
            thomannConversationsRepository.paginator.pages.collect { conversationPages ->
                _uiState.update { it.copy(conversationPages = conversationPages) }
            }
    }
}