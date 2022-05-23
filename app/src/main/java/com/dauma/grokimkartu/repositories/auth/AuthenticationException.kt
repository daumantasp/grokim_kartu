package com.dauma.grokimkartu.repositories.users

class AuthenticationException(error: AuthenticationErrors)
    : Exception(error.toString()) {}

enum class AuthenticationErrors {
    USER_ALREADY_LOGGED_IN,
    USER_NOT_LOGGED_IN,
    EMAIL_NOT_VERIFIED,
    EMAIL_TAKEN,
    INVALID_EMAIL,
    INCORRECT_USR_NAME_OR_PSW,
    INCORRECT_OLD_PSW,
    NEW_PSW_SIMILAR,
    UNKNOWN
}