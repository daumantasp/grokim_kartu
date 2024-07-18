package com.dauma.grokimkartu.models.validators.name

interface NameValidator {
    fun isValid(name: String): Boolean
}