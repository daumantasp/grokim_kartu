package com.dauma.grokimkartu.viewmodels.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val loginForm: LoginForm
) : ViewModel(), LoginViewModel {
    private val _navigateToPlayers = MutableLiveData<Event<Any>>()
    private val _navigateToForgotPassword = MutableLiveData<Event<Int>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    val navigateToPlayers: LiveData<Event<Any>> = _navigateToPlayers
    val navigateToForgotPassword: LiveData<Event<Int>> = _navigateToForgotPassword
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError

    companion object {
        private val TAG = "LoginViewModel"
    }

    override fun loginUser(email: String, password: String) {
        val loginUser = LoginUser(email, password)
        try {
            usersRepository.loginUser(loginUser) { isSuccessful, error ->
                if (isSuccessful) {
                    clearAuthenticationErrors()
                    _navigateToPlayers.value = Event(R.id.action_loginFragment_to_playersFragment)
                } else {
                    Log.d(TAG, error?.message ?: "Login was unsuccessful")
                    if (error != null) {
                        handleAuthenticationError(error)
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }

    override fun forgotPasswordClicked() {
        _navigateToForgotPassword.value = Event(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.INVALID_EMAIL -> {
                _emailError.value = R.string.login_invalid_email_error
                _passwordError.value = -1
            }
            AuthenticationError.INVALID_PASSWORD -> {
                _emailError.value = -1
                _passwordError.value = R.string.login_invalid_password_error
            }
            AuthenticationError.EMAIL_NOT_VERIFIED -> {
                _emailError.value = R.string.login_email_not_verified
                _passwordError.value = -1
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
        _passwordError.value = -1
    }
}