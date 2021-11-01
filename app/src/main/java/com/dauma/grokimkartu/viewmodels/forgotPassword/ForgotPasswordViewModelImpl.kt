package com.dauma.grokimkartu.viewmodels.forgotPassword

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.ForgotPasswordForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.viewmodels.login.LoginViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val forgotPasswordForm: ForgotPasswordForm
) : ViewModel() {
    private val _emailError = MutableLiveData<Int>()
    val emailError: LiveData<Int> = _emailError
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    val showSuccess = _showSuccess
    private val _navigateToLogin = MutableLiveData<Event<Int>>()
    val navigateToLogin: LiveData<Event<Int>> = _navigateToLogin

    companion object {
        private val TAG = "ForgotPasswordViewModel"
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
        _navigateToLogin.value = Event(R.id.action_forgotPasswordFragment_to_loginFragment)
    }

    fun getForgotPasswordForm(): ForgotPasswordForm {
        return forgotPasswordForm
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