package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

interface AuthRepository {
    fun register(email: String, password: String, name: String, onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun login(email: String, password: String, onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun tryReauthenticate(onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun logout(onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun delete(onComplete: (Boolean, AuthenticationErrors?) -> Unit)
    fun changePassword(oldPassword: String, newPassword: String, newPasswordConfirmed: String, onComplete: (Boolean, AuthenticationErrors?) -> Unit)
}