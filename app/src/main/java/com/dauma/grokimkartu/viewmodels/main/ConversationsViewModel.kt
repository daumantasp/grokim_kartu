package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val privateConversationsRepository: PrivateConversationsRepository,
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "ConversationsViewModellImpl"
    }

    fun viewIsReady() {
    }

    fun viewIsDiscarded() {
    }

    fun backClicked() {
    }
}