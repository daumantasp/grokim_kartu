package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.Result

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String): Result<Boolean?, AuthenticationErrors?>
    suspend fun login(email: String, password: String)
    suspend fun tryReauthenticate()
    suspend fun logout()
    suspend fun delete(): Result<Boolean, AuthenticationErrors?>
    suspend fun changePassword(oldPassword: String, newPassword: String, newPasswordConfirmed: String): Result<Boolean, AuthenticationErrors?>
    fun registerLoginListener(id: String, listener: LoginListener)
    fun unregisterLoginListener(id: String)
    fun registerLogoutListener(id: String, listener: LogoutListener)
    fun unregisterLogoutListener(id: String)
}