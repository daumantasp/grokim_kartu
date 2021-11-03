package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
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
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    private val _emailError = MutableLiveData<Int>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val showSuccess: LiveData<Event<Boolean>> = _showSuccess
    val emailError: LiveData<Int> = _emailError

    companion object {
        private val TAG = "ForgotPasswordViewModel"
    }

    fun getForgotPasswordForm(): ForgotPasswordForm {
        return forgotPasswordForm
    }

    fun resetClicked(email: String) {
        usersRepository.sendPasswordResetEmail(email) { isSuccessful, error ->
            if (isSuccessful) {
                _showSuccess.value = Event(true)
            } else {
                Log.d(TAG, error?.message ?: "Password reset was unsuccessful")
                if (error != null) {
                    handleAuthenticationError(error)
                }
            }
        }
    }

    fun okClicked() {
        _navigateToLogin.value = Event("")
    }

    fun backClicked() {
        _navigateToLogin.value = Event("")
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