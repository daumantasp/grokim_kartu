package com.dauma.grokimkartu.models.validators.password

class PasswordValidatorImpl: PasswordValidator {

    override fun isValid(password: String): Boolean = password.length in 6..35

}