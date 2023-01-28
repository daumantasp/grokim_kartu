package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannConversationsViewModel @Inject constructor(
    private val thomannConversationsRepository: ThomannConversationsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _thomannConversations = MutableLiveData<List<Conversation>>()
    val thomannConversations: LiveData<List<Conversation>> = _thomannConversations

    companion object {
        private val TAG = "ThomannConversationsViewModelImpl"
    }

    fun viewIsReady() {
        loadConversations()
        profileRepository.reloadUnreadCount()
    }

    fun viewIsDiscarded() {
    }

    fun backClicked() {
    }

    fun reload() {
        loadConversations()
        profileRepository.reloadUnreadCount()
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