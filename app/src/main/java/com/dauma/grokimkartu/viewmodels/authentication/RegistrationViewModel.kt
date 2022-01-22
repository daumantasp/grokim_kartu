package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.os.Handler
import android.os.Looper
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val registrationForm: RegistrationForm,
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _emailVerificationSent = MutableLiveData<Event<Boolean>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    private val _verificationEmailWillBeAllowedToSentInSeconds = MutableLiveData<Int>()
    private val _enableResendButton = MutableLiveData<Boolean>()
    private val _registrationInProgress = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val emailVerificationSent: LiveData<Event<Boolean>> = _emailVerificationSent
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError
    var verificationEmailWillBeAllowedToSentInSeconds: LiveData<Int> = _verificationEmailWillBeAllowedToSentInSeconds
    val enableResendButton: LiveData<Boolean> = _enableResendButton
    val registrationInProgress: LiveData<Boolean> = _registrationInProgress

    companion object {
        private val TAG = "RegistrationViewModel"
    }

    fun getRegistrationForm() : RegistrationForm {
        return registrationForm
    }

    fun createUser(name: String, email: String, password: String) {
        try {
            usersRepository.registerUser(email, password, name) { isSuccessful, error ->
                if (isSuccessful) {
                    usersRepository.sendEmailVerification()
                    updateEmailVerificationTimer()
                    clearAuthenticationErrors()
                    _emailVerificationSent.value = Event(isSuccessful)
                } else {
                    Log.d(TAG, error?.message ?: "Registration was unsuccessful")
                    if (error != null) {
                        handleAuthenticationError(error)
                    }
                }
                _registrationInProgress.value = false
            }
        } catch (e: AuthenticationException) {
            _registrationInProgress.value = false
            Log.d(TAG, e.message ?: "Registration was unsuccessful")
        }
    }

    fun backClicked() {
        cleanUp()
    }

    fun okClicked() {
        cleanUp()
    }

    fun resendClicked() {
        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
        if (timerValue == 0) {
            usersRepository.sendEmailVerification()
            _verificationEmailWillBeAllowedToSentInSeconds.value = 60
            updateEmailVerificationTimer()
        }
    }

    private fun updateEmailVerificationTimer() {
        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
        if (timerValue == null || timerValue > 0) {
            _verificationEmailWillBeAllowedToSentInSeconds.value = if (timerValue == null) 60 else timerValue - 1
            _enableResendButton.value = false
            Handler(Looper.getMainLooper()).postDelayed({
                updateEmailVerificationTimer()
            }, 1000)
        } else {
            _enableResendButton.value = true
        }
    }

    private fun cleanUp() {
        if (usersRepository.isUserLoggedIn()) {
            usersRepository.logOut()
        }
        _navigateToLogin.value = Event("")
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.EMAIL_ALREADY_REGISTERED -> {
                _emailError.value = R.string.registration_email_already_exists_error
                _passwordError.value = -1
            }
            AuthenticationError.EMAIL_INCORRECT_FORMAT -> {
                _emailError.value = R.string.registration_email_incorrect_format_error
                _passwordError.value = -1
            }
            AuthenticationError.PASSWORD_TOO_WEAK -> {
                _emailError.value = -1
                _passwordError.value = R.string.registration_email_password_too_weak_error
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
        _passwordError.value = -1
    }
}