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
class ConversationsViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository,
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _privateConversations = MutableLiveData<List<Conversation>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val privateConversations: LiveData<List<Conversation>> = _privateConversations

    companion object {
        private val TAG = "ConversationsViewModellImpl"
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