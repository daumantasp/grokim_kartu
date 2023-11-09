package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsManager
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsSettings
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.SettingsForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import com.dauma.grokimkartu.repositories.settings.SettingsRepository
import com.dauma.grokimkartu.repositories.settings.entities.Settings
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val settingsForm: SettingsForm,
    private val pushNotificationsManager: PushNotificationsManager,
    private val utils: Utils
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        data object Loading : UiState()
        data class Loaded(val pushNotificationSettings: PushNotificationsSettings) : UiState()
        data object LogoutStarted : UiState()
        data class LogoutCompleted(val isSuccessful: Boolean) : UiState()
    }

    companion object {
        private val TAG = "SettingsViewModel"
        private const val PUSH_NOTIFICATIONS_CHANGE_PERIODIC = "PUSH_NOTIFICATIONS_CHANGE_PERIODIC"
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_START) {
                    setupPushNotifications()
                }
            }
        })
        viewModelScope.launch {
            observerAuthState()
        }
        viewModelScope.launch {
            loadSettings()
        }
    }

    private suspend fun observerAuthState() {
        authRepository.authState.collect { authState ->
            when (authState) {
                is AuthState.LogoutStarted -> {
                    _uiState.value = UiState.LogoutStarted
                }
                is AuthState.LogoutCompleted -> {
                    _uiState.value = UiState.LogoutCompleted(authState.isSuccessful)
                }
                else -> {}
            }
        }
    }

    fun getSettingsForm() : SettingsForm {
        return settingsForm
    }
    
    private suspend fun loadSettings() {
        val settings = settingsRepository.settings()
        settings.data?.let {
            settingsForm.setInitialValues(it.email, it.isVisible)
        }
    }

    private fun setupPushNotifications() {
        val arePushNotificationSettingsEnabled = pushNotificationsManager.arePushNotificationsSettingsEnabled()
        when (arePushNotificationSettingsEnabled) {
            PushNotificationsSettings.ENABLED_AND_SUBSCRIBED -> settingsForm.arePushNotificationsEnabled = true
            PushNotificationsSettings.ENABLED_NOT_SUBSCRIBED -> settingsForm.arePushNotificationsEnabled = false
            else -> {}
        }
        _uiState.value = UiState.Loaded(arePushNotificationSettingsEnabled)
    }

    fun enablePushNotificationsChanged() {
        utils.dispatcherUtils.main.cancelPeriodic(PUSH_NOTIFICATIONS_CHANGE_PERIODIC)
        utils.dispatcherUtils.main.periodic(
            operationKey = PUSH_NOTIFICATIONS_CHANGE_PERIODIC,
            period = 1.0,
            startImmediately = false,
            repeats = false
        ) {
            if (settingsForm.arePushNotificationsEnabled) {
                pushNotificationsManager.subscribe { _ -> }
            } else {
                pushNotificationsManager.unsubscribe { _ -> }
            }
        }
    }

    fun saveChanges() {
        if (!settingsForm.isChanged()) { return }
        viewModelScope.launch {
            val settingsUpdate = Settings(
                name = null,
                email = null,
                createdAt = null,
                isVisible = settingsForm.isVisible
            )
            val updatedSettings = settingsRepository.update(settingsUpdate)
            updatedSettings.data?.let {
                settingsForm.setInitialValues(it.email, it.isVisible)
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (e: AuthenticationException) {
                Log.d(TAG, e.message ?: "Logout was unsuccessful")
            }
        }
    }
}