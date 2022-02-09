package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.DeleteUserForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeleteUserViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val deleteUserForm: DeleteUserForm
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _passwordError = MutableLiveData<Int>()
    private val _deleteInProgress = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val passwordError: LiveData<Int> = _passwordError
    val deleteInProgress: LiveData<Boolean> = _deleteInProgress

    companion object {
        private val TAG = "DeleteUserViewModel"
    }

    fun getDeleteUserForm() : DeleteUserForm {
        return deleteUserForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun deleteUserClicked() {
        if (deleteUserForm.isPasswordValid() == false) {
            return
        }

        try {
            _deleteInProgress.value = true
            usersRepository.getUserData() { user, exception ->
                if (user?.email != null) {
                    usersRepository.reauthenticateUser(user.email, deleteUserForm.password) { isSuccessful, error ->
                        if (isSuccessful) {
                            usersRepository.deleteUser() { isSuccessful, error ->
                                if (isSuccessful) {
                                    _navigateToLogin.value = Event("")
                                }
                                _deleteInProgress.value = false
                            }
                        } else {
                            Log.d(TAG, error?.message ?: "Reauthentication was unsuccessful")
                            if (error != null) {
                                handleAuthenticationError(error)
                            }
                            _deleteInProgress.value = false
                        }
                    }
                } else {
                    _deleteInProgress.value = false
                }
            }
        } catch (e: AuthenticationException) {
            _deleteInProgress.value = false
            Log.d(TAG, e.message ?: "User delete was unsuccessful")
        }
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.INVALID_PASSWORD -> {
                _passwordError.value = R.string.login_invalid_password_error
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _passwordError.value = -1
    }
}