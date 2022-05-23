package com.dauma.grokimkartu.repositories.settings

class SettingsException(error: SettingsErrors)
    : Exception(error.toString()) {}


enum class SettingsErrors {
    USER_NOT_LOGGED_IN,
    UNKNOWN
}