package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
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
    private val _navigateToDeleteUser = MutableLiveData<Event<String>>()
    private val _navigateToPasswordChange = MutableLiveData<Event<String>>()
    private val _passwordError = MutableLiveData<Int>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val navigateToDeleteUser: LiveData<Event<String>> = _navigateToDeleteUser
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
                user?.email ?: "",
                user?.visible ?: false
            )
        }
    }

    fun deleteUserClicked() {
        _navigateToDeleteUser.value = Event("")
    }

    fun changePassword() {
        _navigateToPasswordChange.value = Event("")
    }

    fun saveChanges() {
        if (settingsForm.isChanged() == false) {
            return
        }

        val email = settingsForm.email
        // I do not let user to change his name
        val newUser = User(
            providerId = null,
            id = null,
            name = null,
            email = null,
            isEmailVerified = null,
            photoUri = null,
            visible = settingsForm.isVisible,
            registrationDate = null
        )

        usersRepository.setUserData(newUser) { isSuccessful, e ->
            Log.d(TAG, "showMeClicked updated successfully")
            this._initialUser = newUser
            this.settingsForm.setInitialValues(
                email,
                newUser.visible ?: false
            )
        }
    }

    fun logoutClicked() {
        try {
            usersRepository.logOut()
            _navigateToLogin.value = Event("")
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
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