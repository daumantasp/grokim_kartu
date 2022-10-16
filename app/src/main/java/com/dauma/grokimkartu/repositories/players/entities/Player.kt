package com.dauma.grokimkartu.repositories.players.entities

import com.dauma.grokimkartu.general.IconLoader

data class Player (
    val userId: Int?,
    val name: String?,
    val instrument: String?,
    val description: String?,
    val iconLoader: IconLoader,
    val city: String?
)