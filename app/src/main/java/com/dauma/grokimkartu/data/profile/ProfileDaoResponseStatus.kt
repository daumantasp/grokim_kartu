package com.dauma.grokimkartu.data.profile

class ProfileDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        PROFILE_NOT_FOUND,
        PHOTO_NOT_FOUND,
        ICON_NOT_FOUND,
        PHOTO_NOT_ATTACHED,
        ATTACHED_PHOTO_IS_INVALID,
        INCORRECT_PHOTO_IMAGE_FORMAT,
        UNKNOWN
    }
}