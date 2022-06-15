package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    val navigateToLogin = _navigateToLogin
    val navigateToPlayers = _navigateToPlayers

    companion object {
        private val TAG = "SplashViewModelImpl"
    }

    fun splashCompleted() {
        authRepository.tryReauthenticate { isSuccessful, authenticationErrors ->
            if (isSuccessful) {
                _navigateToPlayers.value = Event("")
            } else {
                _navigateToLogin.value = Event("")
            }
        }
    }
}