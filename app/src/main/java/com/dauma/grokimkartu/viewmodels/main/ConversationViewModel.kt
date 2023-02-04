package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.conversations.ConversationListener
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository,
    private val thomannConversationsRepository: ThomannConversationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ConversationListener {
    private val userId = savedStateHandle.get<Int>("userId")
    private val thomannId = savedStateHandle.get<Int>("thomannId")
    private val userNameSaved = savedStateHandle.get<String>("userName")
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _newConversationPages = MutableLiveData<List<ConversationPage>>()
    private val _nextConversationPage = MutableLiveData<List<ConversationPage>>()
    private val _messagePosted = MutableLiveData<Event<String>>()
    private val _id = MutableLiveData<Event<String?>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val newConversationPages: LiveData<List<ConversationPage>> = _newConversationPages
    val nextConversationPage: LiveData<List<ConversationPage>> = _nextConversationPage
    val messagePosted: LiveData<Event<String>> = _messagePosted
    val id: LiveData<Event<String?>> = _id

    companion object {
        private val TAG = "ConversationViewModel"
    }

    init {
        privateConversationsRepository.registerListener(TAG, this)
        thomannConversationsRepository.registerListener(TAG, this)
        if (userId != -1) {
            privateConversationsRepository.conversationPartnerId = userId
        } else if (thomannId != -1) {
            thomannConversationsRepository.thomannId = thomannId
        }
    }

    fun viewIsReady() {
        if (userId != -1) {
            _id.value = Event(userNameSaved)
            _newConversationPages.value = privateConversationsRepository.pages
        } else if (thomannId != -1) {
            _id.value = Event("#$thomannId")
            _newConversationPages.value = thomannConversationsRepository.pages
        }
    }

    fun viewIsDiscarded() {
        privateConversationsRepository.unregisterListener(TAG)
        thomannConversationsRepository.unregisterListener(TAG)
        privateConversationsRepository.conversationPartnerId = null
        thomannConversationsRepository.thomannId = null
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun postMessageClicked(messageText: String) {
        if (messageText.isNotBlank()) {
            val postMessage = PostMessage(
                text = messageText
            )
            if (userId != -1) {
                privateConversationsRepository.postMessage(postMessage) { message, conversationsErrors ->
                    _messagePosted.value = Event("")
                }
            } else if (thomannId != -1) {
                thomannConversationsRepository.postMessage(postMessage) { message, conversationsErrors ->
                    _messagePosted.value = Event("")
                }
            }
        }
    }

    fun loadNextConversationPage() {
        if (userId != -1) {
            privateConversationsRepository.loadNextPage { _, _ ->
                _nextConversationPage.value = privateConversationsRepository.pages
            }
        } else if (thomannId != -1) {
            thomannConversationsRepository.loadNextPage { _, _ ->
                _nextConversationPage.value = privateConversationsRepository.pages
            }
        }
    }

    override fun conversationChanged() {
        if (userId != -1) {
            _newConversationPages.value = privateConversationsRepository.pages
        } else if (thomannId != -1) {
            _newConversationPages.value = thomannConversationsRepository.pages
        }
    }
}