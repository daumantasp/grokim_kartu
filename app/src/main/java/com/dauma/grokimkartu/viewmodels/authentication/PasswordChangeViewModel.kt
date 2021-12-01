package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.PasswordChangeForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordChangeViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val passwordChangeForm: PasswordChangeForm
) : ViewModel() {
    private val _navigateToProfile = MutableLiveData<Event<String>>()
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    private val _oldPasswordError = MutableLiveData<Int>()
    private val _newPasswordError = MutableLiveData<Int>()
    private val _repeatPasswordError = MutableLiveData<Int>()
    val navigateToProfile: LiveData<Event<String>> = _navigateToProfile
    val showSuccess: LiveData<Event<Boolean>> = _showSuccess
    val oldPasswordError: LiveData<Int> = _oldPasswordError
    val newPasswordError: LiveData<Int> = _newPasswordError
    val repeatPasswordError: LiveData<Int> = _repeatPasswordError

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
            usersRepository.getUserData { user, exception ->
                val email = user?.email
                if (email != null) {
                    // TODO: maybe reauthenticatge and update should be merged into single method?
                    usersRepository.reauthenticateUser(email, oldPassword) { isSuccessful, error ->
                        if (isSuccessful) {
                            usersRepository.updatePassword(newPassword) { isSuccessful, error ->
                                if (isSuccessful) {
                                    _showSuccess.value = Event(true)
                                } else {
                                    Log.d(TAG, error?.message ?: "Password change was unsuccessful")
                                    if (error != null) {
                                        handleAuthenticationError(error)
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, error?.message ?: "Reauthentication was unsuccessful")
                            if (error != null) {
                                handleAuthenticationError(error)
                            }
                        }
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "PasswordChange was unsuccessful")
        }
    }

    fun okClicked() {
        _navigateToProfile.value = Event("")
    }

    fun backClicked() {
        _navigateToProfile.value = Event("")
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.INVALID_PASSWORD -> {
                _oldPasswordError.value = R.string.passwordChange_invalid_password_error
                _newPasswordError.value = -1
                _repeatPasswordError.value = -1
            }
            AuthenticationError.PASSWORD_TOO_WEAK -> {
                _oldPasswordError.value = -1
                _newPasswordError.value = R.string.registration_email_password_too_weak_error
                _repeatPasswordError.value = -1
            }
            else -> clearErrors()
        }
    }

    private fun clearErrors() {
        _oldPasswordError.value = -1
        _newPasswordError.value = -1
        _repeatPasswordError.value = -1
    }
}