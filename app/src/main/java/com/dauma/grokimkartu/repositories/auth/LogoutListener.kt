package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

interface LogoutListener {
    fun logoutCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?)
}