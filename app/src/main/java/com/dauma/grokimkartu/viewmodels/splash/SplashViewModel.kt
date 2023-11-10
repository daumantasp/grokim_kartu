package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoginSuccessful: Boolean? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

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
                    _uiState.update { it.copy(isLoginSuccessful = authState.isSuccessful) }
                }
                else -> {}
            }
        }
    }
}