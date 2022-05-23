package com.dauma.grokimkartu.repositories.thomanns

class ThomannsException(error: ThomannsErrors)
    : Exception(error.toString()) {}


enum class ThomannsErrors {
    USER_NOT_LOGGED_IN,
    INVALID_VALID_UNTIL_DATE,
    INVALID_AMOUNT,
    UNKNOWN
}