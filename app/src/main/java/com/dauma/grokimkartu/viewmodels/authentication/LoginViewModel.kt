package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsManager
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.ui.authentication.LoginFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginForm: LoginForm,
    private val pushNotificationsManager: PushNotificationsManager,
    private val user: User
) : ViewModel() {

    private val _navigation: MutableStateFlow<NavigationCommand?> = MutableStateFlow(null)
    val navigation: StateFlow<NavigationCommand?> = _navigation

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiState?> = _uiState

    sealed class UiState {
        data object AskForNotificationPermission : UiState()
        data object LoginStarted : UiState()
        data class LoginCompleted(
            val isSuccessful: Boolean,
            val emailError: Int? = null,
            val passwordError: Int? = null
        ) : UiState()
    }

    companion object {
        private val TAG = "LoginViewModel"
        private const val IS_NOTIFICATIONS_PERMISSION_ASKED_KEY = "IS_NOTIFICATIONS_PERMISSION_ASKED_KEY"
    }

    init {
        viewModelScope.launch {
            observeAuthState()
        }
        askForNotificationsPermissionIfAllowed()
    }

    private fun askForNotificationsPermissionIfAllowed() {
        val hasNotificationPermissionShown = user.hasNotificationsPermissionShown ?: false
        if (!hasNotificationPermissionShown) {
            _uiState.value = UiState.AskForNotificationPermission
            user.hasNotificationsPermissionShown = true
        }
    }

    fun enableNotifications(isEnabled: Boolean) {
        if (isEnabled) {
            pushNotificationsManager.subscribe { _ -> }
        } else {
            pushNotificationsManager.unsubscribe { _ -> }
        }
    }

    fun loginUser() {
        if (!loginForm.isValid()) { return }
        viewModelScope.launch {
            try {
                authRepository.login(loginForm.getEmail(), loginForm.getPassword())
            } catch (e: AuthenticationException) {
                Log.d(TAG, e.message ?: "Login was unsuccessful")
            }
        }
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }

    fun backClicked() {
        _navigation.value = NavigationCommand.CloseApp
    }

    private suspend fun observeAuthState() {
        authRepository.authState.collect { authState ->
            when (authState) {
                is AuthState.LoginStarted -> {
                    _uiState.value = UiState.LoginStarted
                }
                is AuthState.LoginCompleted -> {
                    if (authState.isSuccessful) {
                        _uiState.value = UiState.LoginCompleted(isSuccessful = true)
                        _navigation.value = NavigationCommand.ToDirection(LoginFragmentDirections.actionLoginFragmentToHomeGraph())
                    } else if (authState.errors != null) {
                        handleAuthenticationError(authState.errors)
                    } else {
                        _uiState.value = UiState.LoginCompleted(isSuccessful = false)
                    }
                }
                else -> {}
            }
        }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW -> {
                _uiState.value = UiState.LoginCompleted(false, null, R.string.login_incorrect_usr_name_or_psw_error)
            }
            AuthenticationErrors.EMAIL_NOT_VERIFIED -> {
                _uiState.value = UiState.LoginCompleted(false, R.string.login_email_not_verified_error, null)
            }
//            AuthenticationErrors.TOO_MANY_REQUESTS -> {
//                _emailError.value = R.string.login_too_many_requests_error
//                _passwordError.value = -1
//            }
            else -> {
                _uiState.value = UiState.LoginCompleted(false)
            }
        }
    }
}