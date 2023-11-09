package com.dauma.grokimkartu.repositories.users

import android.util.Log
import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoResponseStatus
import com.dauma.grokimkartu.data.auth.entities.*
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
    private val authDao: AuthDao,
    private val user: User,
    private val utils: Utils
) : AuthRepository {
    private val _authState = MutableStateFlow<AuthState?>(null)
    override val authState: StateFlow<AuthState?> = _authState

    companion object {
        private const val USER_ACCESS_TOKEN_KEY = "USER_ACCESS_TOKEN_KEY"
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<Boolean?, AuthenticationErrors?> {
        if (!user.isUserLoggedIn()) {
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
        if (!user.isUserLoggedIn()) {
            _authState.value = AuthState.LoginStarted
            val loginRequest = LoginRequest(email, password)
            val response = authDao.login(loginRequest)
            val status = response.status
            val loginResponse = response.data
            if (status.isSuccessful && loginResponse != null) {
                user.login(loginResponse)
                if (loginResponse.accessToken != null) {
                    saveAccessTokenToStorage(loginResponse.accessToken!!)
                }
                _authState.value = AuthState.LoginCompleted(true, null)
            } else {
                when (status.error) {
                    AuthDaoResponseStatus.Errors.INCORRECT_USR_NAME_OR_PSW -> {
                        _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW)
                    }
                    AuthDaoResponseStatus.Errors.EMAIL_NOT_VERIFIED -> {
                        _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.EMAIL_NOT_VERIFIED)
                    }
                    else -> {
                        _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.UNKNOWN)
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override suspend fun tryReauthenticate() {
        if (!user.isUserLoggedIn()) {
            val accessToken = getAccessTokenFromStorage()
            if (accessToken != null) {
                _authState.value = AuthState.LoginStarted
                val reauthenticateRequest = ReauthenticateRequest(accessToken)
                val response = authDao.reauthenticate(reauthenticateRequest)
                val status = response.status
                val loginResponse = response.data
                if (status.isSuccessful && loginResponse != null) {
                    user.login(loginResponse)
                    if (loginResponse.accessToken != null) {
                        saveAccessTokenToStorage(loginResponse.accessToken!!)
                    }
                    _authState.value = AuthState.LoginCompleted(true, null)
                } else {
                    when (status.error) {
                        AuthDaoResponseStatus.Errors.INCORRECT_ACCESS_TOKEN -> {
                            _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.INCORRECT_ACCESS_TOKEN)
                        }
                        else -> {
                            _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            } else {
                _authState.value = AuthState.LoginCompleted(false, AuthenticationErrors.ACCESS_TOKEN_NOT_PROVIDED)
            }
        } else {
//            Crashes when user receives push notification and app is in the background
//            So instead set Login state
//            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
            _authState.value = AuthState.LoginCompleted(true, null)
        }
    }

    override suspend fun logout() {
        if (user.isUserLoggedIn()) {
            _authState.value = AuthState.LogoutStarted
            val logoutRequest = LogoutRequest(utils.appUtils.deviceId())
            val response = authDao.logout(logoutRequest, user.getBearerAccessToken()!!)
            val status = response.status
            if (status.isSuccessful) {
                user.logout()
                removeAccessTokenFromStorage()
                _authState.value = AuthState.LogoutCompleted(true, null)
            } else {
                _authState.value = AuthState.LogoutCompleted(false, AuthenticationErrors.UNKNOWN)
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