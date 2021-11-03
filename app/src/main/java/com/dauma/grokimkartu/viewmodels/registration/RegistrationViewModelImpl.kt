package com.dauma.grokimkartu.viewmodels.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.os.Handler
import android.os.Looper
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val registrationForm: RegistrationForm,
) : ViewModel(), RegistrationViewModel {
    private val _emailVerificationSent = MutableLiveData<Event<Any>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    private val _navigateToLogin = MutableLiveData<Int>()
    private var _verificationEmailWillBeAllowedToSentInSeconds = MutableLiveData<Int>()
    private val _enableResendButton = MutableLiveData<Boolean>()
    val emailVerificationSent: LiveData<Event<Any>> = _emailVerificationSent
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError
    val navigateToLogin: LiveData<Int> = _navigateToLogin
    val enableResendButton = _enableResendButton
    var verificationEmailWillBeAllowedToSentInSeconds: LiveData<Int> = _verificationEmailWillBeAllowedToSentInSeconds

    companion object {
        private val TAG = "RegistrationViewModel"
    }

    override fun createUser(name: String, email: String, password: String) {
        val registrationUser = RegistrationUser(name, email, password)
        try {
            usersRepository.registerUser(registrationUser) { isSuccessful, error ->
                if (isSuccessful) {
                    usersRepository.sendEmailVerification()
                    updateVerificationEmailTimer()
                    clearAuthenticationErrors()
                    _emailVerificationSent.value = Event(isSuccessful)
                } else {
                    Log.d(TAG, error?.message ?: "Registration was unsuccessful")
                    if (error != null) {
                        handleAuthenticationError(error)
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Registration was unsuccessful")
        }
    }

    fun getRegistrationForm() : RegistrationForm {
        return registrationForm
    }

    fun okClicked() {
        usersRepository.logOut()
        _navigateToLogin.value = R.id.action_registrationFragment_to_loginFragment
    }

    fun resendClicked() {
        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
        if (timerValue == 0) {
            usersRepository.sendEmailVerification()
            _verificationEmailWillBeAllowedToSentInSeconds.value = 60
            updateVerificationEmailTimer()
        }
    }

    private fun updateVerificationEmailTimer() {
        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
        if (timerValue == null) {
            _verificationEmailWillBeAllowedToSentInSeconds.value = 60
            _enableResendButton.value = false
            Handler(Looper.getMainLooper()).postDelayed({
                updateVerificationEmailTimer()
            }, 1000)
        } else if (timerValue > 0) {
            _verificationEmailWillBeAllowedToSentInSeconds.value = timerValue - 1
            _enableResendButton.value = false
            Handler(Looper.getMainLooper()).postDelayed({
                updateVerificationEmailTimer()
            }, 1000)
        } else {
            _enableResendButton.value = true
        }
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