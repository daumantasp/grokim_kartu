package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {
    private val _notificationsLoaded = MutableLiveData<Event<String>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _notificationsPages = MutableLiveData<List<NotificationsPage>>()
    val notificationsLoaded: LiveData<Event<String>> = _notificationsLoaded
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val notificationsPages: LiveData<List<NotificationsPage>> = _notificationsPages

    companion object {
        private val TAG = "NotificationsViewModelImpl"
    }

    fun viewIsReady() {
        if (notificationsRepository.pages.isEmpty()) {
            loadNextNotificationsPage()
        } else {
            _notificationsPages.value = notificationsRepository.pages
        }
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    private fun loadNextNotificationsPage() {
        notificationsRepository.loadNextPage { _, _ ->
            _notificationsPages.value = notificationsRepository.pages
        }
    }
}