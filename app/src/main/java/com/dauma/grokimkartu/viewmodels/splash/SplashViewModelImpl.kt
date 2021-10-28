package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel(), SplashViewModel {
    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>> = _navigateToLogin
    private val _navigateToPlayers = MutableLiveData<Event<Boolean>>()
    val navigateToPlayers = _navigateToPlayers

    companion object {
        private val TAG = "SplashViewModelImpl"
    }

    override fun splashCompleted() {
        if (usersRepository.isUserLoggedIn()) {
            _navigateToPlayers.value = Event(true)
        } else {
            _navigateToLogin.value = Event(true)
        }
    }
}