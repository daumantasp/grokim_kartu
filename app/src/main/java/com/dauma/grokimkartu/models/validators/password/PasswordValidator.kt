package com.dauma.grokimkartu.models.validators.password

interface PasswordValidator {
    fun isValid(password: String): Boolean
}