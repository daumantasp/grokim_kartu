package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor() : ViewModel() {
    companion object {
        private val TAG = "ConversationsViewModelImpl"
    }
}