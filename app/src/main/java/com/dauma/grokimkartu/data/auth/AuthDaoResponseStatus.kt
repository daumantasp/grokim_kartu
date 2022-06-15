package com.dauma.grokimkartu.data.auth

class AuthDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        INCORRECT_USR_NAME_OR_PSW,
        INCORRECT_ACCESS_TOKEN,
        EMAIL_NOT_VERIFIED,
        EMAIL_TAKEN,
        INVALID_EMAIL,
        INCORRECT_OLD_PSW,
        NEW_PSW_SIMILAR,
        PSW_CONFIRMATION_DONT_MATCH,
        UNKNOWN
    }
}

