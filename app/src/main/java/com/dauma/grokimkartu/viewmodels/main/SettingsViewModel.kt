package com.dauma.grokimkartu.viewmodels.main

import android.content.Context
import androidx.lifecycle.*
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsManager
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsSettings
import com.dauma.grokimkartu.general.thememodemanager.ThemeMode
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.general.utils.locale.LocaleUtils
import com.dauma.grokimkartu.models.forms.SettingsForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.LogoutListener
import com.dauma.grokimkartu.repositories.settings.SettingsRepository
import com.dauma.grokimkartu.repositories.settings.entities.Settings
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.ui.main.SettingsFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val settingsForm: SettingsForm,
    private val localeUtils: LocaleUtils,
    private val themeModeManager: ThemeModeManager,
    private val pushNotificationsManager: PushNotificationsManager,
    private val utils: Utils
) : ViewModel(), LogoutListener {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _passwordError = MutableLiveData<Int>()
    private val _language = MutableLiveData<Event<Language>>()
    private val _themeMode = MutableLiveData<Event<ThemeMode>>()
    private val _pushNotificationsSettingsEnabled = MutableLiveData<Event<PushNotificationsSettings>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val passwordError: LiveData<Int> = _passwordError
    val language: LiveData<Event<Language>> = _language
    val themeMode: LiveData<Event<ThemeMode>> = _themeMode
    val pushNotificationsSettingsEnabled: LiveData<Event<PushNotificationsSettings>> = _pushNotificationsSettingsEnabled

    companion object {
        private val TAG = "SettingsViewModel"
        private const val SETTINGS_VIEW_MODEL_LOGOUT_LISTENER = "SETTINGS_VIEW_MODEL_LOGOUT_LISTENER"
        private const val PUSH_NOTIFICATIONS_CHANGE_PERIODIC = "PUSH_NOTIFICATIONS_CHANGE_PERIODIC"
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_START) {
                    setupPushNotificationsState()
                }
            }
        })
    }

    fun viewIsReady(context: Context) {
        authRepository.registerLogoutListener(SETTINGS_VIEW_MODEL_LOGOUT_LISTENER, this)
        selectLanguage(context)
        selectThemeMode()
    }

    fun viewIsDiscarded() {
        authRepository.unregisterLogoutListener(SETTINGS_VIEW_MODEL_LOGOUT_LISTENER)
    }

    fun getSettingsForm() : SettingsForm {
        return settingsForm
    }
    
    fun loadSettings() {
//        settingsRepository.settings { settings, settingsErrors ->
//            if (settings != null) {
//                this.settingsForm.setInitialValues(
//                    email = settings.email,
//                    isVisible = settings.isVisible
//                )
//            }
//        }
        setupPushNotificationsState()
    }

    private fun setupPushNotificationsState() {
        val arePushNotificationSettingsEnabled = pushNotificationsManager.arePushNotificationsSettingsEnabled()
        when (arePushNotificationSettingsEnabled) {
            PushNotificationsSettings.ENABLED_AND_SUBSCRIBED -> settingsForm.arePushNotificationsEnabled = true
            PushNotificationsSettings.ENABLED_NOT_SUBSCRIBED -> settingsForm.arePushNotificationsEnabled = false
            else -> {}
        }
        _pushNotificationsSettingsEnabled.value = Event(arePushNotificationSettingsEnabled)
    }

    private fun selectLanguage(context: Context) {
        val currentLanguage = localeUtils.getCurrentLanguage(context)
        _language.value = Event(currentLanguage)
    }

    private fun selectThemeMode() {
        _themeMode.value = Event(themeModeManager.currentThemeMode)
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
        if (settingsForm.isChanged()) {
            val settingsUpdate = Settings(
                name = null,
                email = null,
                createdAt = null,
                isVisible = settingsForm.isVisible
            )
//            settingsRepository.update(settingsUpdate) { settings, settingsErrors ->
//                if (settings != null) {
//                    this.settingsForm.setInitialValues(
//                        email = settings.email,
//                        isVisible = settings.isVisible
//                    )
//                }
//            }
        }
    }

    fun logoutClicked() {
//        authRepository.logout()
    }

    override fun logoutCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            _navigation.value = Event(NavigationCommand.ToDirection(SettingsFragmentDirections.actionSettingsFragmentToAuthGraph()))
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