package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.data.users.entitites.FirestoreUser
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.SettingsForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val settingsForm: SettingsForm
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToPasswordChange = MutableLiveData<Event<String>>()
    private val _passwordError = MutableLiveData<Int>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val navigateToPasswordChange: LiveData<Event<String>> = _navigateToPasswordChange
    val passwordError: LiveData<Int> = _passwordError

    companion object {
        private val TAG = "SettingsViewModel"
    }

    fun getSettingsForm() : SettingsForm {
        return settingsForm
    }

    fun deleteUser(password: String) {
        try {
            val email = usersRepository.getAuthenticatedUserData().email
            if (email != null) {
                usersRepository.reauthenticateUser(email, password) { isSuccessful, error ->
                    if (isSuccessful) {
                        usersRepository.deleteUser() { isSuccessful, error ->
                            if (isSuccessful) {
                                _navigateToLogin.value = Event("")
                            }
                        }
                    } else {
                        Log.d(SettingsViewModel.TAG, error?.message ?: "Reauthentication was unsuccessful")
                        if (error != null) {
                            handleAuthenticationError(error)
                        }
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(SettingsViewModel.TAG, e.message ?: "User delete was unsuccessful")
        }
    }

    fun passwordChangeClicked() {
        _navigateToPasswordChange.value = Event("")
    }

    private var currentUserData: FirestoreUser? = null
    fun showMeClicked(isOn: Boolean) {
        // PROOF OF CONCEPT
        if (currentUserData == null) {
            usersRepository.getUserData { user, e ->
                currentUserData = user

                var updatedShowMe: Boolean? = null
                if (user?.showMe == true) {
                    updatedShowMe = false
                } else {
                    updatedShowMe = true
                }

                var newUserData = FirestoreUser()
                newUserData.id = user?.id ?: ""
                newUserData.showMe = updatedShowMe
                this.usersRepository.setUserData(newUserData) { isSuccessful, e ->
                    Log.d(TAG, "showMeClicked updated successfully")
                }
            }
        }

        Log.d(TAG, "showMeClicked isOn=$isOn")
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