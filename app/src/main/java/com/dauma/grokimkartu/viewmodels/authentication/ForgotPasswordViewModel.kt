package com.dauma.grokimkartu.viewmodels.authentication

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.forms.ForgotPasswordForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ForgotPasswordUiState(
    val isPasswordResetInProgress: Boolean = false,
    val isPasswordResetSuccessful: Boolean = false,
    val emailError: Int? = null,
    val close: Boolean = false
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val forgotPasswordForm: ForgotPasswordForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "ForgotPasswordViewModel"
    }

    fun getForgotPasswordForm() = forgotPasswordForm

    fun resetClicked() {
        if (!forgotPasswordForm.isEmailValid()) { return }
        _uiState.update { it.copy(isPasswordResetInProgress = true) }

//        _passwordResetInProgress.value = true
//        authRepository.sendPasswordResetEmail(email) { isSuccessful, error ->
//            if (isSuccessful) {
//                _showSuccess.value = Event(true)
//            } else {
//                Log.d(TAG, error?.message ?: "Password reset was unsuccessful")
//                if (error != null) {
//                    handleAuthenticationError(error)
//                }
//            }
//            _passwordResetInProgress.value = false
//        }
    }

    fun okClicked() {
        _uiState.update { it.copy(close = true) }
    }

    fun backClicked() {
        _uiState.update { it.copy(close = true) }
    }

//    private fun handleAuthenticationError(error: AuthenticationError) {
//        when(error.message) {
//            AuthenticationError.INVALID_EMAIL -> {
//                _emailError.value = R.string.passwordChange_invalid_email_error
//            }
//            else -> clearAuthenticationErrors()
//        }
//    }

    private fun clearAuthenticationErrors() {
        _uiState.update { it.copy(
            isPasswordResetInProgress = false,
            isPasswordResetSuccessful = false,
            emailError = null
        ) }
    }
}