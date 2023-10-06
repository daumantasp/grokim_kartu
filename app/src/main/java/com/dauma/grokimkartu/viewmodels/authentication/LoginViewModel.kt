package com.dauma.grokimkartu.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.pushnotificationsmanager.PushNotificationsManager
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.ui.authentication.LoginFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginForm: LoginForm,
    private val pushNotificationsManager: PushNotificationsManager,
    private val user: User,
    private val utils: Utils
) : ViewModel(), LoginListener {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    private val _loginInProgress = MutableLiveData<Boolean>()
    private val _askForNotificationsPermissionIfAllowed = MutableLiveData<Event<String>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError
    val loginInProgress: LiveData<Boolean> = _loginInProgress
    val askForNotificationsPermissionIfAllowed: LiveData<Event<String>> = _askForNotificationsPermissionIfAllowed

    companion object {
        private val TAG = "LoginViewModel"
        private const val LOGIN_VIEW_MODEL_LOGIN_LISTENER_ID = "LOGIN_VIEW_MODEL_LOGIN_LISTENER_ID"
        private const val IS_NOTIFICATIONS_PERMISSION_ASKED_KEY = "IS_NOTIFICATIONS_PERMISSION_ASKED_KEY"
    }

    fun viewIsReady() {
        authRepository.registerLoginListener(LOGIN_VIEW_MODEL_LOGIN_LISTENER_ID, this)
        askForNotificationsPermissionIfAllowed()
    }

    fun viewIsDiscarded() {
        authRepository.unregisterLoginListener(LOGIN_VIEW_MODEL_LOGIN_LISTENER_ID)
    }

    private fun askForNotificationsPermissionIfAllowed() {
        val hasNotificationPermissionShown = user.hasNotificationsPermissionShown ?: false
        if (!hasNotificationPermissionShown) {
            _askForNotificationsPermissionIfAllowed.value = Event("")
            user.hasNotificationsPermissionShown = true
        }
    }

    fun enableNotifications(isEnabled: Boolean) {
        if (isEnabled) {
            pushNotificationsManager.subscribe { _ -> }
        } else {
            pushNotificationsManager.unsubscribe { _ -> }
        }
    }

    fun loginUser(email: String, password: String) {
        try {
            _loginInProgress.value = true
//            authRepository.login(email, password)
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.CloseApp)
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            clearAuthenticationErrors()
            _navigation.value = Event(NavigationCommand.ToDirection(LoginFragmentDirections.actionLoginFragmentToHomeGraph()))
        } else {
            if (errors != null) {
                handleAuthenticationError(errors)
            }
        }
        _loginInProgress.value = false
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
            AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW -> {
                _emailError.value = -1
                _passwordError.value = R.string.login_incorrect_usr_name_or_psw_error
            }
            AuthenticationErrors.EMAIL_NOT_VERIFIED -> {
                _emailError.value = R.string.login_email_not_verified_error
                _passwordError.value = -1
            }
//            AuthenticationErrors.TOO_MANY_REQUESTS -> {
//                _emailError.value = R.string.login_too_many_requests_error
//                _passwordError.value = -1
//            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
        _passwordError.value = -1
    }
}