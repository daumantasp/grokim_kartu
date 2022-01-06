package com.dauma.grokimkartu.repositories.players.entities

data class Player (
    val userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?,
    val icon: PlayerIcon,
    val city: String?
)