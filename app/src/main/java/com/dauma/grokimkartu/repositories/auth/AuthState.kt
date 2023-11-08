package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

sealed class AuthState {
    object LoginStarted : AuthState()
    object LogoutStarted : AuthState()
    data class LoginCompleted(
        val isSuccessful: Boolean,
        val errors: AuthenticationErrors?,
    ): AuthState()
    data class LogoutCompleted(
        val isSuccessful: Boolean,
        val errors: AuthenticationErrors?,
    ): AuthState()
}