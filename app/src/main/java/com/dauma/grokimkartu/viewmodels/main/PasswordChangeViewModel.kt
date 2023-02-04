package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
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
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    private val _oldPasswordError = MutableLiveData<Int>()
    private val _newPasswordError = MutableLiveData<Int>()
    private val _repeatPasswordError = MutableLiveData<Int>()
    private val _changeInProgress = MutableLiveData<Boolean>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val showSuccess: LiveData<Event<Boolean>> = _showSuccess
    val oldPasswordError: LiveData<Int> = _oldPasswordError
    val newPasswordError: LiveData<Int> = _newPasswordError
    val repeatPasswordError: LiveData<Int> = _repeatPasswordError
    val changeInProgress: LiveData<Boolean> = _changeInProgress

    companion object {
        private val TAG = "PasswordChangeViewModel"
    }

    fun passwordChangeForm() : PasswordChangeForm {
        return passwordChangeForm
    }

    fun passwordChangeClicked(oldPassword: String, newPassword: String, repeatPassword: String) {
        if (passwordChangeForm.isOldAndNewPasswordSame()) {
            clearErrors()
            _newPasswordError.value = R.string.password_change_old_and_new_password_must_not_be_the_same_error
            return
        }
        if (passwordChangeForm.isNewAndRepeatPasswordSame() == false) {
            clearErrors()
            _repeatPasswordError.value = R.string.password_change_new_and_repeat_password_are_not_the_same_error
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
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_OLD_PSW -> {
                _oldPasswordError.value = R.string.password_change_invalid_password_error
                _newPasswordError.value = -1
                _repeatPasswordError.value = -1
            }
            AuthenticationErrors.NEW_PSW_SIMILAR -> {
                _oldPasswordError.value = -1
                _newPasswordError.value = R.string.password_change_old_and_new_password_must_not_be_the_same_error
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