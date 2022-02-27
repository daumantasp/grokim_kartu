package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _userProfile = MutableLiveData<Profile>()
    private val _userIcon = MutableLiveData<Bitmap?>()
    private val _navigateToProfile = MutableLiveData<Event<String>>()
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    val userProfile: LiveData<Profile> = _userProfile
    val userIcon: LiveData<Bitmap?> = _userIcon
    val navigateToProfile: LiveData<Event<String>> = _navigateToProfile
    val navigateToPlayers = _navigateToPlayers

    companion object {
        private val TAG = "HomeViewModel"
    }

    fun viewIsReady() {
        loadUserProfile()
        loadUserIcon()
    }

    fun userIconClicked() {
        _navigateToProfile.value = Event("")
    }

    private fun loadUserIcon() {
        usersRepository.getUserIcon() { icon, e ->
            this._userIcon.value = icon
        }
    }

    private fun loadUserProfile() {
        usersRepository.getUserProfile() { profile, e ->
            if (profile != null) {
                this._userProfile.value = profile
            }
        }
    }
}