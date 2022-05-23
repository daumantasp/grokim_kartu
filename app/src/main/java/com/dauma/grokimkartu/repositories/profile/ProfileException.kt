package com.dauma.grokimkartu.repositories.profile

class ProfileException(error: ProfileErrors)
    : Exception(error.toString()) {}


enum class ProfileErrors {
    USER_NOT_LOGGED_IN,
    PHOTO_NOT_FOUND,
    ICON_NOT_FOUND,
    ATTACHED_PHOTO_IS_INVALID,
    UNKNOWN
}