package com.dauma.grokimkartu.repositories.players

class PlayersException(error: PlayersErrors)
    : Exception(error.toString()) {}


enum class PlayersErrors {
    USER_NOT_LOGGED_IN,
    ICON_NOT_FOUND,
    PHOTO_NOT_FOUND,
    UNKNOWN
}