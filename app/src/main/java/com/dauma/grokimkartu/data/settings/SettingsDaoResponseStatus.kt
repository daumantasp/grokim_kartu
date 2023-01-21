package com.dauma.grokimkartu.data.settings

class SettingsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        PROFILE_NOT_FOUND,
        TOKEN_IS_NOT_PROVIDED,
        DEVICE_ID_IS_NOT_PROVIDED,
        UNKNOWN
    }
}