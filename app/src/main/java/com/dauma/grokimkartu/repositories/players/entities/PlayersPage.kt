package com.dauma.grokimkartu.repositories.players.entities

data class PlayersPage (
    val players: List<Player>?,
    val isLast: Boolean
)