package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginForm: LoginForm
) : ViewModel() {
    private val _navigateToPlayers = MutableLiveData<Event<String>>()
    private val _navigateToRegistration = MutableLiveData<Event<String>>()
    private val _navigateToForgotPassword = MutableLiveData<Event<String>>()
    private val _closeApp = MutableLiveData<Event<String>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    private val _loginInProgress = MutableLiveData<Boolean>()
    val navigateToPlayers: LiveData<Event<String>> = _navigateToPlayers
    val navigateToRegistration: LiveData<Event<String>> = _navigateToRegistration
    val navigateToForgotPassword: LiveData<Event<String>> = _navigateToForgotPassword
    val closeApp: LiveData<Event<String>> = _closeApp
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError
    val loginInProgress: LiveData<Boolean> = _loginInProgress

    companion object {
        private val TAG = "LoginViewModel"
    }

    fun loginUser(email: String, password: String) {
        try {
            _loginInProgress.value = true
            authRepository.login(email, password) { isSuccessful, error ->
                if (isSuccessful) {
                    clearAuthenticationErrors()
                    _navigateToPlayers.value = Event("")
                } else {
                    if (error != null) {
                        handleAuthenticationError(error)
                    }
                }
                _loginInProgress.value = false
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }

    fun registrationClicked() {
        _navigateToRegistration.value = Event("")
    }

    fun forgotPasswordClicked() {
        _navigateToForgotPassword.value = Event("")
    }

    fun backClicked() {
        _closeApp.value = Event("")
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW -> {
                _emailError.value = -1
                _passwordError.value = R.string.login_incorrect_usr_name_or_psw
            }
            AuthenticationErrors.EMAIL_NOT_VERIFIED -> {
                _emailError.value = R.string.login_email_not_verified
                _passwordError.value = -1
            }
//            AuthenticationErrors.TOO_MANY_REQUESTS -> {
//                _emailError.value = R.string.login_too_many_requests_error
//                _passwordError.value = -1
//            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
        _passwordError.value = -1
    }
}