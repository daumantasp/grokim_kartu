package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.forms.PasswordChangeForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PasswordChangeUiState(
    val isPasswordChangeStarted: Boolean = false,
    val isPasswordChangeSuccessful: Boolean = false,
    val oldPasswordError: Int = -1,
    val newPasswordError: Int = -1,
    val repeatPasswordError: Int = -1,
    val close: Boolean = false
)

@HiltViewModel
class PasswordChangeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val passwordChangeForm: PasswordChangeForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordChangeUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "PasswordChangeViewModel"
    }

    fun passwordChangeForm() : PasswordChangeForm {
        return passwordChangeForm
    }

    fun back() = _uiState.update { it.copy(isPasswordChangeSuccessful = false, close = true) }

    fun passwordChangeClicked() {
        if (!passwordChangeForm.isValid())
            return
        if (passwordChangeForm.isOldAndNewPasswordSame()) {
            _uiState.update { it.copy(
                isPasswordChangeStarted = false,
                oldPasswordError = -1,
                newPasswordError = R.string.password_change_old_and_new_password_must_not_be_the_same_error,
                repeatPasswordError = -1
            ) }
            return
        }
        if (!passwordChangeForm.isNewAndRepeatPasswordSame()) {
            _uiState.update { it.copy(
                isPasswordChangeStarted = false,
                oldPasswordError = -1,
                newPasswordError = -1,
                repeatPasswordError = R.string.password_change_new_and_repeat_password_are_not_the_same_error
            ) }
            return
        }

        try {
            _uiState.update { it.copy(isPasswordChangeStarted = true) }
            viewModelScope.launch {
                val changePasswordResponse = authRepository.changePassword(
                    oldPassword = passwordChangeForm.getOldPassword(),
                    newPassword = passwordChangeForm.getNewPassword(),
                    newPasswordConfirmed = passwordChangeForm.getRepeatPassword()
                )
                val isSuccessful = changePasswordResponse.data
                if (isSuccessful)
                    _uiState.update { it.copy(isPasswordChangeStarted = false, isPasswordChangeSuccessful = true) }
                else {
                    changePasswordResponse.error?.let {
                        handleAuthenticationError(it)
                    }
                }
            }
        } catch (e: AuthenticationException) {
            _uiState.update { it.copy(isPasswordChangeStarted = false, isPasswordChangeSuccessful = false) }
            Log.d(TAG, e.message ?: "PasswordChange failed")
        }
    }

    fun okClicked() {
        _uiState.update { it.copy(isPasswordChangeSuccessful = false, close = true) }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_OLD_PSW -> {
                _uiState.update { it.copy(
                    isPasswordChangeStarted = false,
                    oldPasswordError = R.string.password_change_invalid_password_error,
                    newPasswordError = -1,
                    repeatPasswordError = -1
                ) }
            }
            AuthenticationErrors.NEW_PSW_SIMILAR -> {
                _uiState.update { it.copy(
                    isPasswordChangeStarted = false,
                    oldPasswordError = -1,
                    newPasswordError = R.string.password_change_old_and_new_password_must_not_be_the_same_error,
                    repeatPasswordError = -1
                ) }
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
        _uiState.update { it.copy(
            isPasswordChangeStarted = false,
            oldPasswordError = -1,
            newPasswordError = -1,
            repeatPasswordError = -1
        ) }
    }
}