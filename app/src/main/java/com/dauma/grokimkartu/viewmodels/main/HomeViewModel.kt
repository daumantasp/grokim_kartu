package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {
    private val _name = MutableLiveData<String?>()
    private val _userIcon = MutableLiveData<Bitmap?>()
    private val _unreadCount = MutableLiveData<Int?>()
    private val _navigateToProfile = MutableLiveData<Event<String>>()
    private val _navigateToNotifications = MutableLiveData<Event<String>>()
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    private val _navigateToThomann = MutableLiveData<Event<String>>()
    val name: LiveData<String?> = _name
    val userIcon: LiveData<Bitmap?> = _userIcon
    val unreadCount: LiveData<Int?> = _unreadCount
    val navigateToProfile: LiveData<Event<String>> = _navigateToProfile
    val navigateToNotifications: LiveData<Event<String>> = _navigateToNotifications
    val navigateToPlayers = _navigateToPlayers
    val navigateToThomann = _navigateToThomann

    companion object {
        private val TAG = "HomeViewModel"
    }

    fun viewIsReady() {
        loadUserProfile()
        loadUserIcon()
        loadUnreadNotificationsCount()
    }

    fun userIconClicked() {
        _navigateToProfile.value = Event("")
    }

    fun notificationsClicked() {
        _navigateToNotifications.value = Event("")
    }

    fun playersClicked() {
        _navigateToPlayers.value = Event("")
    }

    fun thomannClicked() {
        _navigateToThomann.value = Event("")
    }

    private fun loadUserIcon() {
        profileRepository.icon() { icon, profileErrors ->
            this._userIcon.value = icon
        }
    }

    private fun loadUserProfile() {
        profileRepository.profile { profile, _ ->
            _name.value = profile?.name
        }
    }

    private fun loadUnreadNotificationsCount() {
        notificationsRepository.unreadCount { unreadCount, notificationsErrors ->
            _unreadCount.value = unreadCount
        }
    }
}