package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoResponseStatus
import com.dauma.grokimkartu.data.auth.entities.ChangePasswordRequest
import com.dauma.grokimkartu.data.auth.entities.LoginRequest
import com.dauma.grokimkartu.data.auth.entities.ReauthenticateRequest
import com.dauma.grokimkartu.data.auth.entities.RegistrationRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.auth.LogoutListener

class AuthRepositoryImpl(
    private val authDao: AuthDao,
    private val user: User,
    private val utils: Utils
) : AuthRepository {
    private val loginListeners: MutableMap<String, LoginListener> = mutableMapOf()
    private val logoutListeners: MutableMap<String, LogoutListener> = mutableMapOf()

    companion object {
        private const val USER_ACCESS_TOKEN_KEY = "USER_ACCESS_TOKEN_KEY"
    }

    override fun register(
        email: String,
        password: String,
        name: String
    ) {
        if (user.isUserLoggedIn() == false) {
            val registrationRequest = RegistrationRequest(
                name = name,
                email = email,
                password = password,
                passwordConfirmation = password
            )
            authDao.register(registrationRequest) { registrationResponse, authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful && registrationResponse != null) {
                    user.login(registrationResponse)
                    notifyLoginListeners(true, null)
                } else {
                    when (authDaoResponseStatus.error) {
                        AuthDaoResponseStatus.Errors.EMAIL_TAKEN -> {
                            notifyLoginListeners(false, AuthenticationErrors.EMAIL_TAKEN)
                        }
                        AuthDaoResponseStatus.Errors.INVALID_EMAIL -> {
                            notifyLoginListeners(false, AuthenticationErrors.INVALID_EMAIL)
                        }
                        else -> {
                            notifyLoginListeners(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override fun login(
        email: String,
        password: String
    ) {
        if (user.isUserLoggedIn() == false) {
            val loginRequest = LoginRequest(email, password)
            authDao.login(loginRequest) { loginResponse, authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful == true && loginResponse != null) {
                    user.login(loginResponse)
                    if (loginResponse.accessToken != null) {
                        saveAccessTokenToStorage(loginResponse.accessToken!!)
                    }
                    notifyLoginListeners(true, null)
                } else {
                    when (authDaoResponseStatus.error) {
                        AuthDaoResponseStatus.Errors.INCORRECT_USR_NAME_OR_PSW -> {
                            notifyLoginListeners(false, AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW)
                        }
                        AuthDaoResponseStatus.Errors.EMAIL_NOT_VERIFIED -> {
                            notifyLoginListeners(false, AuthenticationErrors.EMAIL_NOT_VERIFIED)
                        }
                        else -> {
                            notifyLoginListeners(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override fun tryReauthenticate() {
        if (user.isUserLoggedIn() == false) {
            val accessToken = getAccessTokenFromStorage()
            if (accessToken != null) {
                val reauthenticateRequest = ReauthenticateRequest(accessToken)
                authDao.reauthenticate(reauthenticateRequest) { loginResponse, authDaoResponseStatus ->
                    if (authDaoResponseStatus.isSuccessful == true && loginResponse != null) {
                        user.login(loginResponse)
                        if (loginResponse.accessToken != null) {
                            saveAccessTokenToStorage(loginResponse.accessToken!!)
                        }
                        this.notifyLoginListeners(true, null)
                    } else {
                        when (authDaoResponseStatus.error) {
                            AuthDaoResponseStatus.Errors.INCORRECT_ACCESS_TOKEN -> {
                                notifyLoginListeners(false, AuthenticationErrors.INCORRECT_ACCESS_TOKEN)
                            }
                            else -> {
                                notifyLoginListeners(false, AuthenticationErrors.UNKNOWN)
                            }
                        }
                    }
                }
            } else {
                notifyLoginListeners(false, AuthenticationErrors.ACCESS_TOKEN_NOT_PROVIDED)
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override fun logout() {
        if (user.isUserLoggedIn()) {
            authDao.logout(user.getBearerAccessToken()!!) { authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful) {
                    user.logout()
                    removeAccessTokenFromStorage()
                    notifyLogoutListeners(true, null)
                } else {
                    notifyLogoutListeners(false, AuthenticationErrors.UNKNOWN)
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun delete(onComplete: (Boolean, AuthenticationErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            authDao.delete(user.getBearerAccessToken()!!) { authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful) {
                    user.logout()
                    removeAccessTokenFromStorage()
                    onComplete(true, null)
                } else {
                    onComplete(false, AuthenticationErrors.UNKNOWN)
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirmed: String,
        onComplete: (Boolean, AuthenticationErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val changePasswordRequest = ChangePasswordRequest(oldPassword, newPassword, newPasswordConfirmed)
            authDao.changePassword(user.getBearerAccessToken()!!, changePasswordRequest) { authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful) {
                    onComplete(true, null)
                } else {
                    when (authDaoResponseStatus.error) {
                        AuthDaoResponseStatus.Errors.INCORRECT_OLD_PSW -> {
                            onComplete(false, AuthenticationErrors.INCORRECT_OLD_PSW)
                        }
                        AuthDaoResponseStatus.Errors.NEW_PSW_SIMILAR -> {
                            onComplete(false, AuthenticationErrors.NEW_PSW_SIMILAR)
                        }
                        else -> {
                            onComplete(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun registerLoginListener(id: String, listener: LoginListener) {
        loginListeners[id] = listener
    }

    override fun unregisterLoginListener(id: String) {
        loginListeners.remove(id)
    }

    private fun notifyLoginListeners(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        for (listener in loginListeners.values) {
            listener.loginCompleted(isSuccessful, errors)
        }
    }

    override fun registerLogoutListener(id: String, listener: LogoutListener) {
        logoutListeners[id] = listener
    }

    override fun unregisterLogoutListener(id: String) {
        logoutListeners.remove(id)
    }

    private fun notifyLogoutListeners(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        for (listener in logoutListeners.values) {
            listener.logoutCompleted(isSuccessful, errors)
        }
    }

    private fun getAccessTokenFromStorage() : String? {
        return utils.sharedStorageUtils.getEntry(USER_ACCESS_TOKEN_KEY)
    }

    private fun saveAccessTokenToStorage(accessToken: String) {
        utils.sharedStorageUtils.save(USER_ACCESS_TOKEN_KEY, accessToken)
    }

    private fun removeAccessTokenFromStorage() {
        utils.sharedStorageUtils.remove(USER_ACCESS_TOKEN_KEY)
    }
}