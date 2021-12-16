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
    private var _initialUser: User? = null
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
    
    fun loadSettings() {
        usersRepository.getUserData { user, e ->
            this._initialUser = user
            this.settingsForm.setInitialValues(
                user?.name ?: "",
                user?.visible ?: false
            )
        }
    }

    fun deleteUser() {
        if (settingsForm.isPasswordValid() == false) {
            return
        }

        try {
            usersRepository.getUserData() { user, exception ->
                if (user?.email != null) {
                    usersRepository.reauthenticateUser(user.email, settingsForm.password) { isSuccessful, error ->
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

    fun changePassword() {
        _navigateToPasswordChange.value = Event("")
    }

    fun saveChanges() {
        if (settingsForm.isChanged() == false) {
            return
        }

        val newUser = User(
            _initialUser?.providerId,
            _initialUser?.id,
            _initialUser?.name,
            _initialUser?.email,
            _initialUser?.photoUrl,
            settingsForm.isVisible,
            null
        )

        usersRepository.setUserData(newUser) { isSuccessful, e ->
            Log.d(TAG, "showMeClicked updated successfully")
            this._initialUser = newUser
            this.settingsForm.setInitialValues(
                newUser.name ?: "",
                newUser.visible ?: false
            )
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