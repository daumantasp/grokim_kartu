package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannConversationsViewModel @Inject constructor(
    private val thomannConversationsRepository: ThomannConversationsRepository,
) : ViewModel() {
    private val _thomannConversations = MutableLiveData<List<Conversation>>()
    private val _message = MutableLiveData<Event<Array<Any>>>() // TODO: refactor
    val thomannConversations: LiveData<List<Conversation>> = _thomannConversations
    val message: LiveData<Event<Array<Any>>> = _message

    companion object {
        private val TAG = "ThomannConversationsViewModelImpl"
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

    fun conversationClicked(thomannId: Int, name: String) {
        _message.value = Event(arrayOf<Any>(thomannId, name))
    }

    private fun loadConversations() {
        thomannConversationsRepository.thomannConversations { conversations, conversationsErrors ->
            conversations?.let {
                val newConversations: MutableList<Conversation> = mutableListOf()
                newConversations.addAll(it)
                _thomannConversations.value = newConversations
            }
        }
    }
}