package com.dauma.grokimkartu.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationUiState(
    val isRegistrationStarted: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val close: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val registrationForm: RegistrationForm,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

//    private val _emailVerificationSent = MutableLiveData<Event<Boolean>>()
//    private val _verificationEmailWillBeAllowedToSentInSeconds = MutableLiveData<Int>()
//    private val _enableResendButton = MutableLiveData<Boolean>()
//    val emailVerificationSent: LiveData<Event<Boolean>> = _emailVerificationSent
//    var verificationEmailWillBeAllowedToSentInSeconds: LiveData<Int> = _verificationEmailWillBeAllowedToSentInSeconds
//    val enableResendButton: LiveData<Boolean> = _enableResendButton

    companion object {
        private val TAG = "RegistrationViewModel"
        private const val REGISTRATION_VIEW_MODEL_LOGIN_LISTENER_ID = "REGISTRATION_VIEW_MODEL_LOGIN_LISTENER_ID"
    }

    fun getRegistrationForm() = registrationForm

    fun registrationClicked() {
        if (!registrationForm.isValid()) { return }
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrationStarted = true) }
            val registrationResponse = authRepository.register(
                email = registrationForm.getEmail(),
                password = registrationForm.getPassword(),
                name = registrationForm.getName()
            )
            val isSuccessful = registrationResponse.data == true
            if (isSuccessful) {
//                updateEmailVerificationTimer()
                clearAuthenticationErrors()
                _uiState.update { it.copy(isRegistrationStarted = false, isRegistrationSuccessful = true) }
            } else if (registrationResponse.error != null) {
                handleAuthenticationError(registrationResponse.error)
            } else {
                clearAuthenticationErrors()
            }
        }
    }

    fun backClicked() {
        _uiState.update { it.copy(close = true) }
    }

    fun okClicked() {
        _uiState.update { it.copy(close = true) }
    }

    fun resendClicked() {
//        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
//        if (timerValue == 0) {
//            authRepository.sendEmailVerification()
//            _verificationEmailWillBeAllowedToSentInSeconds.value = 60
//            updateEmailVerificationTimer()
//        }
    }

    private fun updateEmailVerificationTimer() {
//        val timerValue = _verificationEmailWillBeAllowedToSentInSeconds.value
//        if (timerValue == null || timerValue > 0) {
//            _verificationEmailWillBeAllowedToSentInSeconds.value = if (timerValue == null) 60 else timerValue - 1
//            _enableResendButton.value = false
//            Handler(Looper.getMainLooper()).postDelayed({
//                updateEmailVerificationTimer()
//            }, 1000)
//        } else {
//            _enableResendButton.value = true
//        }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.EMAIL_TAKEN -> {
                _uiState.update { it.copy(
                    isRegistrationStarted = false,
                    isRegistrationSuccessful = false,
                    emailError = R.string.registration_email_already_exists_error,
                    passwordError = null
                ) }
            }
            AuthenticationErrors.INVALID_EMAIL -> {
                _uiState.update { it.copy(
                    isRegistrationStarted = false,
                    isRegistrationSuccessful = false,
                    emailError = R.string.registration_email_incorrect_format_error,
                    passwordError = null
                ) }
            }
//            AuthenticationErrors.PASSWORD_TOO_WEAK -> {
//                _emailError.value = -1
//                _passwordError.value = R.string.registration_email_password_too_weak_error
//            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _uiState.update { it.copy(
            isRegistrationStarted = false,
            emailError = null,
            passwordError = null
        ) }
    }
}