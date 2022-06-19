package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {
    private val _notificationsLoaded = MutableLiveData<Event<String>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    val notificationsLoaded: LiveData<Event<String>> = _notificationsLoaded
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "NotificationsViewModelImpl"
    }

    fun viewIsReady() {
        loadNotifications()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    private fun loadNotifications() {

    }
}