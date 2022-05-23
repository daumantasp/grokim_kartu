package com.dauma.grokimkartu.data.auth

import com.dauma.grokimkartu.data.auth.entities.*

interface AuthDao {
    fun register(registrationRequest: RegistrationRequest, onComplete: (LoginResponse?, AuthDaoResponseStatus) -> Unit)
    fun login(loginRequest: LoginRequest, onComplete: (LoginResponse?, AuthDaoResponseStatus) -> Unit)
    fun logout(accessToken: String, onComplete: (AuthDaoResponseStatus) -> Unit)
    fun delete(accessToken: String, onComplete: (AuthDaoResponseStatus) -> Unit)
    fun changePassword(accessToken: String, changePasswordRequest: ChangePasswordRequest, onComplete: (AuthDaoResponseStatus) -> Unit)
}