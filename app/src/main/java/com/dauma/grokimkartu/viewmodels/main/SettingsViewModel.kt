package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.SettingsForm
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val settingsForm: SettingsForm
) : ViewModel() {
    private var initialUser: User? = null
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToPasswordChange = MutableLiveData<Event<String>>()
    private val _passwordError = MutableLiveData<Int>()
    private val _user = MutableLiveData<User>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val navigateToPasswordChange: LiveData<Event<String>> = _navigateToPasswordChange
    val passwordError: LiveData<Int> = _passwordError
    val user: LiveData<User> = _user

    companion object {
        private val TAG = "SettingsViewModel"
    }

    fun getSettingsForm() : SettingsForm {
        return settingsForm
    }
    
    fun loadSettings() {
        usersRepository.getUserData { user, e ->
            if (user != null) {
                initialUser = user
                _user.value = user
            }
        }
    }

    fun deleteUser(password: String) {
        try {
            usersRepository.getUserData() { user, exception ->
                if (user?.email != null) {
                    usersRepository.reauthenticateUser(user.email, password) { isSuccessful, error ->
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
            }
        } catch (e: AuthenticationException) {
            Log.d(SettingsViewModel.TAG, e.message ?: "User delete was unsuccessful")
        }
    }

    fun passwordChangeClicked() {
        _navigateToPasswordChange.value = Event("")
    }

    fun showMeClicked(isOn: Boolean) {
        _user.value = User(
            initialUser?.providerId,
            initialUser?.id,
            initialUser?.name,
            initialUser?.email,
            initialUser?.photoUrl,
            isOn
        )
    }

    fun saveChangesClicked() {
        val user = _user.value
        if (user != null) {
            usersRepository.setUserData(user) { isSuccessful, e ->
                Log.d(TAG, "showMeClicked updated successfully")
            }
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