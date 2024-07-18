package com.dauma.grokimkartu.models.validators.email

import android.util.Patterns

class EmailValidatorImpl: EmailValidator {

    override fun isValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
