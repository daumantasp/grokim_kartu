package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsManager
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoginStarted: Boolean = false,
    val isLoginSuccessful: Boolean? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isRegistrationStarted: Boolean = false,
    val isForgotPasswordStarted: Boolean = false,
    val closeApp: Boolean = false,
    val askForNotificationPermissionDialog: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginForm: LoginForm,
    private val pushNotificationsManager: PushNotificationsManager,
    private val user: User
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

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

    fun getLoginForm() : LoginForm = loginForm

    fun back() = _uiState.update { it.copy(closeApp = true) }

    fun registration() = _uiState.update { it.copy(isRegistrationStarted = true) }

    fun forgotPassword() = _uiState.update { it.copy(isForgotPasswordStarted = true) }

    fun notificationPermissionsDialogShown() = _uiState.update { it.copy(askForNotificationPermissionDialog = false) }

    fun registrationStarted() = _uiState.update { it.copy(isRegistrationStarted = false) }

    fun forgotPasswordStarted() = _uiState.update { it.copy(isForgotPasswordStarted = false) }

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

    private fun askForNotificationsPermissionIfAllowed() {
        val hasNotificationPermissionShown = user.hasNotificationsPermissionShown ?: false
        if (!hasNotificationPermissionShown) {
            _uiState.update { it.copy(askForNotificationPermissionDialog = true) }
            user.hasNotificationsPermissionShown = true
        }
    }

    private suspend fun observeAuthState() {
        authRepository.authState.collect { authState ->
            when (authState) {
                is AuthState.LoginStarted -> {
                    _uiState.update { it.copy(
                        isLoginStarted = true,
                        isLoginSuccessful = null,
                        emailError = null,
                        passwordError = null
                    ) }
                }
                is AuthState.LoginCompleted -> {
                    if (authState.isSuccessful) {
                        _uiState.update { it.copy(
                            isLoginStarted = false,
                            isLoginSuccessful = true,
                            emailError = null,
                            passwordError = null
                        ) }
                    } else if (authState.errors != null) {
                        handleAuthenticationError(authState.errors)
                    } else {
                        _uiState.update { it.copy(
                            isLoginStarted = false,
                            isLoginSuccessful = false,
                            emailError = null,
                            passwordError = null
                        ) }
                    }
                }
                else -> {}
            }
        }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW -> {
                _uiState.update { it.copy(
                    isLoginStarted = false,
                    isLoginSuccessful = false,
                    emailError = null,
                    passwordError = R.string.login_incorrect_usr_name_or_psw_error
                ) }
            }
            AuthenticationErrors.EMAIL_NOT_VERIFIED -> {
                _uiState.update { it.copy(
                    isLoginStarted = false,
                    isLoginSuccessful = false,
                    emailError = R.string.login_email_not_verified_error,
                    passwordError = null
                ) }
            }
//            AuthenticationErrors.TOO_MANY_REQUESTS -> {
//                _emailError.value = R.string.login_too_many_requests_error
//                _passwordError.value = -1
//            }
            else -> {
                _uiState.update { it.copy(
                    isLoginStarted = false,
                    isLoginSuccessful = false,
                    emailError = null,
                    passwordError = null
                ) }
            }
        }
    }
}