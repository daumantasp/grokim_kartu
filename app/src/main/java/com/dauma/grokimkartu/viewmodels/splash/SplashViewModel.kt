package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import com.dauma.grokimkartu.ui.splash.SplashFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigation: MutableStateFlow<NavigationCommand?> = MutableStateFlow(null)
    val navigation: StateFlow<NavigationCommand?> = _navigation

    init {
        viewModelScope.launch {
            observeAuthState()
        }
        viewModelScope.launch {
            authRepository.tryReauthenticate()
        }
    }

    private suspend fun observeAuthState() {
        authRepository.authState.collect { authState ->
            when (authState) {
                is AuthState.LoginCompleted -> {
                    val navDirection = if (authState.isSuccessful) {
                        SplashFragmentDirections.actionSplashFragmentToHomeGraph()
                    } else {
                        SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                    }
                    _navigation.value = NavigationCommand.ToDirection(navDirection)
                }
                else -> {}
            }
        }
    }
}