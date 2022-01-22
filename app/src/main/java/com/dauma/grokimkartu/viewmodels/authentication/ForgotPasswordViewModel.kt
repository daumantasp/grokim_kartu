package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ForgotPasswordForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val forgotPasswordForm: ForgotPasswordForm
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordResetInProgress = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val showSuccess: LiveData<Event<Boolean>> = _showSuccess
    val emailError: LiveData<Int> = _emailError
    val passwordResetInProgress = _passwordResetInProgress

    companion object {
        private val TAG = "ForgotPasswordViewModel"
    }

    fun getForgotPasswordForm(): ForgotPasswordForm {
        return forgotPasswordForm
    }

    fun resetClicked(email: String) {
        _passwordResetInProgress.value = true
        usersRepository.sendPasswordResetEmail(email) { isSuccessful, error ->
            if (isSuccessful) {
                _showSuccess.value = Event(true)
            } else {
                Log.d(TAG, error?.message ?: "Password reset was unsuccessful")
                if (error != null) {
                    handleAuthenticationError(error)
                }
            }
            _passwordResetInProgress.value = false
        }
    }

    fun okClicked() {
        _navigateBack.value = Event("")
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.INVALID_EMAIL -> {
                _emailError.value = R.string.passwordReset_invalid_email_error
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
    }
}