package com.dauma.grokimkartu.models.validators.newpassword

interface NewPasswordValidator {

    fun isValid(old: String, repeat: String, new: String): Boolean

}