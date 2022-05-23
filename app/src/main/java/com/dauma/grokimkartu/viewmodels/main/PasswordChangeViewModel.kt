package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.PasswordChangeForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordChangeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val passwordChangeForm: PasswordChangeForm
) : ViewModel() {
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    private val _oldPasswordError = MutableLiveData<Int>()
    private val _newPasswordError = MutableLiveData<Int>()
    private val _repeatPasswordError = MutableLiveData<Int>()
    private val _changeInProgress = MutableLiveData<Boolean>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    val showSuccess: LiveData<Event<Boolean>> = _showSuccess
    val oldPasswordError: LiveData<Int> = _oldPasswordError
    val newPasswordError: LiveData<Int> = _newPasswordError
    val repeatPasswordError: LiveData<Int> = _repeatPasswordError
    val changeInProgress: LiveData<Boolean> = _changeInProgress
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "PasswordChangeViewModel"
    }

    fun passwordChangeForm() : PasswordChangeForm {
        return passwordChangeForm
    }

    fun passwordChangeClicked(oldPassword: String, newPassword: String, repeatPassword: String) {
        if (passwordChangeForm.isOldAndNewPasswordSame()) {
            clearErrors()
            _newPasswordError.value = R.string.passwordChange_oldAndNewPasswordMustNotBeTheSame_error
            return
        }
        if (passwordChangeForm.isNewAndRepeatPasswordSame() == false) {
            clearErrors()
            _repeatPasswordError.value = R.string.passwordChange_newAndRepeatPasswordAreNotTheSame_error
            return
        }

        try {
            _changeInProgress.value = true
            authRepository.changePassword(oldPassword, newPassword, repeatPassword) { isSuccessful, authenticationErrors ->
                if (isSuccessful) {
                    _showSuccess.value = Event(true)
                } else {
                    if (authenticationErrors != null) {
                        handleAuthenticationError(authenticationErrors)
                    }
                }
                _changeInProgress.value = false
            }
        } catch (e: AuthenticationException) {
            _changeInProgress.value = false
            Log.d(TAG, e.message ?: "PasswordChange was unsuccessful")
        }
    }

    fun okClicked() {
        _navigateBack.value = Event("")
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_OLD_PSW -> {
                _oldPasswordError.value = R.string.passwordChange_invalid_password_error
                _newPasswordError.value = -1
                _repeatPasswordError.value = -1
            }
            AuthenticationErrors.NEW_PSW_SIMILAR -> {
                _oldPasswordError.value = -1
                _newPasswordError.value = R.string.passwordChange_oldAndNewPasswordMustNotBeTheSame_error
                _repeatPasswordError.value = -1
            }
//            AuthenticationErrors.PASSWORD_TOO_WEAK -> {
//                _oldPasswordError.value = -1
//                _newPasswordError.value = R.string.registration_email_password_too_weak_error
//                _repeatPasswordError.value = -1
//            }
            else -> clearErrors()
        }
    }

    private fun clearErrors() {
        _oldPasswordError.value = -1
        _newPasswordError.value = -1
        _repeatPasswordError.value = -1
    }
}