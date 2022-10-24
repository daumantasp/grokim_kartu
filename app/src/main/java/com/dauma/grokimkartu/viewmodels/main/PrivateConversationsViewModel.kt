package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrivateConversationsViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository
) : ViewModel() {
    private val _privateConversations = MutableLiveData<List<Conversation>>()
    private val _message = MutableLiveData<Event<Array<Any>>>() // TODO: refactor
    val privateConversations: LiveData<List<Conversation>> = _privateConversations
    val message: LiveData<Event<Array<Any>>> = _message

    companion object {
        private val TAG = "PrivateConversationsViewModelImpl"
    }

    fun viewIsReady() {
        loadConversations()
    }

    fun viewIsDiscarded() {
    }

    fun backClicked() {
    }

    fun reload() {
        loadConversations()
    }

    fun conversationClicked(userId: Int, name: String) {
        _message.value = Event(arrayOf<Any>(userId, name))
    }

    private fun loadConversations() {
        privateConversationsRepository.conversations { conversations, conversationsErrors ->
            conversations?.let {
                val newConversations: MutableList<Conversation> = mutableListOf()
                newConversations.addAll(it)
                _privateConversations.value = newConversations
            }
        }
    }
}