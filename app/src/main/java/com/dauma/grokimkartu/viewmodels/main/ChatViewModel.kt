package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val userId = savedStateHandle.get<Int>("userId")

    companion object {
        private val TAG = "ChatViewModel"
    }
}