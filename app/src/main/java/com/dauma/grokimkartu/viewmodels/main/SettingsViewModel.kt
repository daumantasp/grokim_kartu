package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.SettingsForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.LogoutListener
import com.dauma.grokimkartu.repositories.settings.SettingsRepository
import com.dauma.grokimkartu.repositories.settings.entities.Settings
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val settingsForm: SettingsForm
) : ViewModel(), LogoutListener {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _navigateToDeleteUser = MutableLiveData<Event<String>>()
    private val _navigateToLanguages = MutableLiveData<Event<String>>()
    private val _navigateToPasswordChange = MutableLiveData<Event<String>>()
    private val _passwordError = MutableLiveData<Int>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val navigateToDeleteUser: LiveData<Event<String>> = _navigateToDeleteUser
    val navigateToLanguages: LiveData<Event<String>> = _navigateToLanguages
    val navigateToPasswordChange: LiveData<Event<String>> = _navigateToPasswordChange
    val passwordError: LiveData<Int> = _passwordError

    companion object {
        private val TAG = "SettingsViewModel"
        private const val SETTINGS_VIEW_MODEL_LOGOUT_LISTENER = "SETTINGS_VIEW_MODEL_LOGOUT_LISTENER"
    }

    fun viewIsReady() {
        authRepository.registerLogoutListener(SETTINGS_VIEW_MODEL_LOGOUT_LISTENER, this)
    }

    fun viewIsDiscarded() {
        authRepository.unregisterLogoutListener(SETTINGS_VIEW_MODEL_LOGOUT_LISTENER)
    }

    fun getSettingsForm() : SettingsForm {
        return settingsForm
    }
    
    fun loadSettings() {
        settingsRepository.settings { settings, settingsErrors ->
            if (settings != null) {
                this.settingsForm.setInitialValues(
                    email = settings.email,
                    isVisible = settings.isVisible
                )
            }
        }
    }

    fun deleteUserClicked() {
        _navigateToDeleteUser.value = Event("")
    }

    fun changeLanguage() {
        _navigateToLanguages.value = Event("")
    }

    fun changePassword() {
        _navigateToPasswordChange.value = Event("")
    }

    fun saveChanges() {
        if (settingsForm.isChanged() == false) {
            return
        }

        val settingsUpdate = Settings(
            name = null,
            email = null,
            createdAt = null,
            isVisible = settingsForm.isVisible
        )
        settingsRepository.update(settingsUpdate) { settings, settingsErrors ->
            if (settings != null) {
                this.settingsForm.setInitialValues(
                    email = settings.email,
                    isVisible = settings.isVisible
                )
            }
        }
    }

    fun logoutClicked() {
        authRepository.logout()
    }

    override fun logoutCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            _navigateToLogin.value = Event("")
        }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
//            AuthenticationErrors.INVALID_PASSWORD -> {
//                _passwordError.value = R.string.login_invalid_password_error
//            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _passwordError.value = -1
    }
}