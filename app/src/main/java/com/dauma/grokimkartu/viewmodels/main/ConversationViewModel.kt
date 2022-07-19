package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.conversations.ConversationListener
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationsRepository: PrivateConversationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ConversationListener {
    private val userId = savedStateHandle.get<Int>("userId")
    private val userNameSaved = savedStateHandle.get<String>("userName")
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _conversationPages = MutableLiveData<List<ConversationPage>>()
    private val _messagePosted = MutableLiveData<Event<String>>()
    private val _userName = MutableLiveData<Event<String?>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val conversationPages: LiveData<List<ConversationPage>> = _conversationPages
    val messagePosted: LiveData<Event<String>> = _messagePosted
    val userName: LiveData<Event<String?>> = _userName

    companion object {
        private val TAG = "ConversationViewModel"
    }

    init {
        conversationsRepository.registerListener(TAG, this)
        conversationsRepository.conversationPartnerId = userId
    }

    fun viewIsReady() {
        _userName.value = Event(userNameSaved)
        _conversationPages.value = conversationsRepository.pages
    }

    fun viewIsDiscarded() {
        conversationsRepository.unregisterListener(TAG)
        conversationsRepository.conversationPartnerId = null
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun postMessageClicked(messageText: String) {
        if (messageText.isNotBlank()) {
            val postMessage = PostMessage(
                text = messageText
            )
            conversationsRepository.postMessage(postMessage) { message, conversationsErrors ->
                _messagePosted.value = Event("")
            }
        }
    }

//    private fun loadNextConversationsPage() {
//        conversationsRepository.loadNextPage { _, _ ->
//            _conversationPages.value = conversationsRepository.pages
//        }
//    }

    override fun conversationChanged() {
        _conversationPages.value = conversationsRepository.pages
    }
}