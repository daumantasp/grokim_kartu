package com.dauma.grokimkartu.data.settings

class SettingsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        PROFILE_NOT_FOUND,
        UNKNOWN
    }
}