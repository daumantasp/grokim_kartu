package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), LoginListener {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    val navigateToLogin = _navigateToLogin
    val navigateToPlayers = _navigateToPlayers

    companion object {
        private val TAG = "SplashViewModelImpl"
        private const val SPLASH_VIEW_MODEL_LOGIN_LISTENER_ID = "SPLASH_VIEW_MODEL_LOGIN_LISTENER_ID"
    }

    fun viewIsReady() {
        authRepository.registerLoginListener(SPLASH_VIEW_MODEL_LOGIN_LISTENER_ID, this)
        authRepository.tryReauthenticate()
    }

    fun viewIsDiscarded() {
        authRepository.unregisterLoginListener(SPLASH_VIEW_MODEL_LOGIN_LISTENER_ID)
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            _navigateToPlayers.value = Event("")
        } else {
            _navigateToLogin.value = Event("")
        }
    }
}