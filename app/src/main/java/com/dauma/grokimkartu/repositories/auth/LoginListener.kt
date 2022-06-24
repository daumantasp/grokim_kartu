package com.dauma.grokimkartu.repositories.auth

import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

interface LoginListener {
    fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?)
}