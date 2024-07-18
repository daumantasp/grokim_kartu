package com.dauma.grokimkartu.models.validators.newpassword

import com.dauma.grokimkartu.models.validators.password.PasswordValidator

class NewPasswordValidatorImpl(
    private val passwordValidator: PasswordValidator
): NewPasswordValidator {
    override fun isValid(old: String, repeat: String, new: String): Boolean = passwordValidator.isValid(new)
                && old == repeat
                && old.isNotEmpty()
                && new.isNotEmpty()
}