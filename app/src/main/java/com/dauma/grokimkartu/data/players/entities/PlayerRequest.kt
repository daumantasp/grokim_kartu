package com.dauma.grokimkartu.data.players.entities

data class PlayerRequest(
    val page: Int,
    val pageSize: Int,
    val cityId: Int? = null,
    val instrumentId: Int? = null,
    val text: String? = null
)