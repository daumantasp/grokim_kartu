package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.PasswordChangeForm
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordChangeViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val passwordChangeForm: PasswordChangeForm
) : ViewModel() {
    private val _navigateToProfile = MutableLiveData<Event<Int>>()
    val navigateToProfile: LiveData<Event<Int>> = _navigateToProfile
    private val _oldPasswordError = MutableLiveData<Int>()
    private val _newPasswordError = MutableLiveData<Int>()
    private val _repeatPasswordError = MutableLiveData<Int>()
    val oldPasswordError: LiveData<Int> = _oldPasswordError
    val newPasswordError: LiveData<Int> = _newPasswordError
    val repeatPasswordError: LiveData<Int> = _repeatPasswordError
    private val _showSuccess = MutableLiveData<Event<Boolean>>()
    val showSuccess = _showSuccess

    companion object {
        private val TAG = "PasswordChangeViewModel"
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
            val email = usersRepository.getAuthenticatedUserData().email
            if (email != null) {
                val loginUser = LoginUser(email, oldPassword)
                usersRepository.reauthenticateUser(loginUser) { isSuccessful, error ->
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
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "PasswordChange was unsuccessful")
        }
    }

    fun passwordChangeForm() : PasswordChangeForm {
        return passwordChangeForm
    }

    fun okClicked() {
        _navigateToProfile.value = Event(R.id.action_passwordChangeFragment_to_profileFragment)
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