package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

interface AuthRepository {
    fun register(email: String, password: String, name: String, onComplete: (Boolean?, AuthenticationErrors?) -> Unit)
    fun login(email: String, password: String)
    fun tryReauthenticate()
    fun logout()
    fun delete(onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun changePassword(oldPassword: String, newPassword: String, newPasswordConfirmed: String, onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun registerLoginListener(id: String, listener: LoginListener)
    fun unregisterLoginListener(id: String)
    fun registerLogoutListener(id: String, listener: LogoutListener)
    fun unregisterLogoutListener(id: String)
}