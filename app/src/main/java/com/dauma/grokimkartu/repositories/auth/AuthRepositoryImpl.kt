package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoResponseStatus
import com.dauma.grokimkartu.data.auth.entities.*
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.Result
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

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<Boolean?, AuthenticationErrors?> {
        if (user.isUserLoggedIn() == false) {
            val registrationRequest = RegistrationRequest(
                name = name,
                email = email,
                password = password,
                passwordConfirmation = password
            )
            val response = authDao.register(registrationRequest)
            val status = response.status
            val registrationResponse = response.data
            if (status.isSuccessful && registrationResponse != null) {
                return Result(true, null)
            } else {
                when (status.error) {
                    AuthDaoResponseStatus.Errors.EMAIL_TAKEN -> {
                        return Result(false, AuthenticationErrors.EMAIL_TAKEN)
                    }
                    AuthDaoResponseStatus.Errors.INVALID_EMAIL -> {
                        return Result(false, AuthenticationErrors.INVALID_EMAIL)
                    }
                    else -> {
                        return Result(false, AuthenticationErrors.UNKNOWN)
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ) {
        if (user.isUserLoggedIn() == false) {
            val loginRequest = LoginRequest(email, password)
            val response = authDao.login(loginRequest)
            val status = response.status
            val loginResponse = response.data
            if (status.isSuccessful && loginResponse != null) {
                user.login(loginResponse)
                if (loginResponse.accessToken != null) {
                    saveAccessTokenToStorage(loginResponse.accessToken!!)
                }
                notifyLoginListeners(true, null)
            } else {
                when (status.error) {
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
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override suspend fun tryReauthenticate() {
        if (user.isUserLoggedIn() == false) {
            val accessToken = getAccessTokenFromStorage()
            if (accessToken != null) {
                val reauthenticateRequest = ReauthenticateRequest(accessToken)
                val response = authDao.reauthenticate(reauthenticateRequest)
                val status = response.status
                val loginResponse = response.data
                if (status.isSuccessful && loginResponse != null) {
                    user.login(loginResponse)
                    if (loginResponse.accessToken != null) {
                        saveAccessTokenToStorage(loginResponse.accessToken!!)
                    }
                    notifyLoginListeners(true, null)
                } else {
                    when (status.error) {
                        AuthDaoResponseStatus.Errors.INCORRECT_ACCESS_TOKEN -> {
                            notifyLoginListeners(false, AuthenticationErrors.INCORRECT_ACCESS_TOKEN)
                        }
                        else -> {
                            notifyLoginListeners(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            } else {
                notifyLoginListeners(false, AuthenticationErrors.ACCESS_TOKEN_NOT_PROVIDED)
            }
        } else {
//            Crashes when user receives push notification and app is in the background
//            So instead notify login listeners
//            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
            this.notifyLoginListeners(true, null)
        }
    }

    override suspend fun logout() {
        if (user.isUserLoggedIn()) {
            val logoutRequest = LogoutRequest(utils.appUtils.deviceId())
            val response = authDao.logout(logoutRequest, user.getBearerAccessToken()!!)
            val status = response.status
            if (status.isSuccessful) {
                user.logout()
                removeAccessTokenFromStorage()
                notifyLogoutListeners(true, null)
            } else {
                notifyLogoutListeners(false, AuthenticationErrors.UNKNOWN)
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun delete(): Result<Boolean, AuthenticationErrors?> {
        if (user.isUserLoggedIn()) {
            val response = authDao.delete(user.getBearerAccessToken()!!)
            val status = response.status
            if (status.isSuccessful) {
                user.logout()
                removeAccessTokenFromStorage()
                return Result(true, null)
            } else {
                return Result(false, AuthenticationErrors.UNKNOWN)
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirmed: String
    ): Result<Boolean, AuthenticationErrors?> {
        if (user.isUserLoggedIn()) {
            val changePasswordRequest = ChangePasswordRequest(oldPassword, newPassword, newPasswordConfirmed)
            val response = authDao.changePassword(user.getBearerAccessToken()!!, changePasswordRequest)
            val status = response.status
            if (status.isSuccessful) {
                return Result(true, null)
            } else {
                when (status.error) {
                    AuthDaoResponseStatus.Errors.INCORRECT_OLD_PSW -> {
                        return Result(false, AuthenticationErrors.INCORRECT_OLD_PSW)
                    }
                    AuthDaoResponseStatus.Errors.NEW_PSW_SIMILAR -> {
                        return Result(false, AuthenticationErrors.NEW_PSW_SIMILAR)
                    }
                    else -> {
                        return Result(false, AuthenticationErrors.UNKNOWN)
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