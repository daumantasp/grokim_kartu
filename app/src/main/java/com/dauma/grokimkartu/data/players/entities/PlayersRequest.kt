package com.dauma.grokimkartu.data.players.entities

data class PlayersRequest(
    val page: Int,
    val pageSize: Int,
    val cityId: Int? = null,
    val instrumentId: Int? = null,
    val text: String? = null
)