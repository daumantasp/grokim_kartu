package com.dauma.grokimkartu.models.validators.email

interface EmailValidator {
    fun isValid(email: String): Boolean
}