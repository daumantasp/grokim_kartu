package com.dauma.grokimkartu.models.validators.name

class NameValidatorImpl: NameValidator {
    override fun isValid(name: String): Boolean = name.length in 1..20
}