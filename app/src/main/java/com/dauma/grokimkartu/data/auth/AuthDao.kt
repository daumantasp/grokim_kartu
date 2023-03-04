package com.dauma.grokimkartu.data.auth

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.auth.entities.*

interface AuthDao {
    suspend fun register(registrationRequest: RegistrationRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus>
    suspend fun login(loginRequest: LoginRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus>
    suspend fun reauthenticate(reauthenticateRequest: ReauthenticateRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus>
    suspend fun logout(logoutRequest: LogoutRequest, accessToken: String): DaoResult<Nothing?, AuthDaoResponseStatus>
    suspend fun delete(accessToken: String): DaoResult<Nothing?, AuthDaoResponseStatus>
    suspend fun changePassword(accessToken: String, changePasswordRequest: ChangePasswordRequest): DaoResult<Nothing?, AuthDaoResponseStatus>
}