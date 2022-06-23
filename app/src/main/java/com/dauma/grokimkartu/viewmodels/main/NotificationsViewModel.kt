package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.notifications.NotificationsListener
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel(), NotificationsListener {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _notificationsUpdated = MutableLiveData<List<NotificationsPage>>()
    private val _notificationsPages = MutableLiveData<List<NotificationsPage>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val notificationsUpdated: LiveData<List<NotificationsPage>> = _notificationsUpdated
    val notificationsPages: LiveData<List<NotificationsPage>> = _notificationsPages

    companion object {
        private val TAG = "NotificationsViewModelImpl"
        private val NOTIFICATIONS_VIEW_MODEL_LISTENER_ID = "NOTIFICATIONS_VIEW_MODEL_LISTENER_ID"
    }

    fun viewIsReady() {
        if (notificationsRepository.pages.isEmpty()) {
            loadNextNotificationsPage()
        } else {
            _notificationsPages.value = notificationsRepository.pages
        }
        notificationsRepository.registerListener(NOTIFICATIONS_VIEW_MODEL_LISTENER_ID, this)
    }

    fun viewIsDiscarded() {
        notificationsRepository.unregisterListener(NOTIFICATIONS_VIEW_MODEL_LISTENER_ID)
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun notificationClicked(notificationId: Int) {
        notificationsRepository.activate(notificationId) { _, _ ->
            _notificationsUpdated.value = notificationsRepository.pages
        }
    }

    fun loadNextNotificationsPage() {
        notificationsRepository.loadNextPage { _, _ ->
            _notificationsPages.value = notificationsRepository.pages
        }
    }

    override fun notificationsChanged() {
        _notificationsPages.value = notificationsRepository.pages
    }
}