package com.dauma.grokimkartu.viewmodels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        data object Loading : UiState()
        data class LoginCompleted(val isSuccessful: Boolean) : UiState()
    }

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
                    _uiState.value = UiState.LoginCompleted(authState.isSuccessful)
                }
                else -> {}
            }
        }
    }
}