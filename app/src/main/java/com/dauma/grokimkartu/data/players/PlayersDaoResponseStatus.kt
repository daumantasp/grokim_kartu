package com.dauma.grokimkartu.data.players

class PlayersDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        FORBIDDEN,
        PLAYER_NOT_FOUND,
        ICON_NOT_FOUND,
        PHOTO_NOT_FOUND,
        UNKNOWN
    }
}