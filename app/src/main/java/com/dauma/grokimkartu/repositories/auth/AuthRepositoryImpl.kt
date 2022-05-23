package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoResponseStatus
import com.dauma.grokimkartu.data.auth.entities.ChangePasswordRequest
import com.dauma.grokimkartu.data.auth.entities.LoginRequest
import com.dauma.grokimkartu.data.auth.entities.RegistrationRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.AuthRepository

class AuthRepositoryImpl(
    private val authDao: AuthDao,
    private val user: User
) : AuthRepository {

    override fun register(
        email: String,
        password: String,
        name: String,
        onComplete: (Boolean, AuthenticationErrors?) -> Unit
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
                    onComplete(true, null)
                } else {
                    when (authDaoResponseStatus.error) {
                        AuthDaoResponseStatus.Errors.EMAIL_TAKEN -> {
                            onComplete(false, AuthenticationErrors.EMAIL_TAKEN)
                        }
                        AuthDaoResponseStatus.Errors.INVALID_EMAIL -> {
                            onComplete(false, AuthenticationErrors.INVALID_EMAIL)
                        }
                        else -> {
                            onComplete(false, AuthenticationErrors.UNKNOWN)
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
        password: String,
        onComplete: (Boolean, AuthenticationErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn() == false) {
            val loginRequest = LoginRequest(email, password)
            authDao.login(loginRequest) { loginResponse, authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful == true && loginResponse != null) {
                    user.login(loginResponse)
                    onComplete(true, null)
                } else {
                    when (authDaoResponseStatus.error) {
                        AuthDaoResponseStatus.Errors.INCORRECT_USR_NAME_OR_PSW -> {
                            onComplete(false, AuthenticationErrors.INCORRECT_USR_NAME_OR_PSW)
                        }
                        AuthDaoResponseStatus.Errors.EMAIL_NOT_VERIFIED -> {
                            onComplete(false, AuthenticationErrors.EMAIL_NOT_VERIFIED)
                        }
                        else -> {
                            onComplete(false, AuthenticationErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw AuthenticationException(AuthenticationErrors.USER_ALREADY_LOGGED_IN)
        }
    }

    override fun logout(onComplete: (Boolean, AuthenticationErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            authDao.logout(user.getBearerAccessToken()!!) { authDaoResponseStatus ->
                if (authDaoResponseStatus.isSuccessful) {
                    user.logout()
                    onComplete(true, null)
                } else {
                    onComplete(false, AuthenticationErrors.UNKNOWN)
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
}