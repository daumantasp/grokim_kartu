package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    val navigateToLogin = _navigateToLogin
    val navigateToPlayers = _navigateToPlayers

    companion object {
        private val TAG = "SplashViewModelImpl"
    }

    fun splashCompleted() {
        if (usersRepository.isUserLoggedIn()) {
            if (usersRepository.isEmailVerified()) {
                _navigateToPlayers.value = Event("")
            } else {
                usersRepository.logOut()
                _navigateToLogin.value = Event("")
            }
        } else {
            _navigateToLogin.value = Event("")
        }
    }
}