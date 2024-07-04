package com.dauma.grokimkartu.repositories.players

class PlayersFilter(
    val cityId: Int?,
    val instrumentId: Int?,
    val text: String?
) {
    companion object {
        val CLEAR = PlayersFilter(
            cityId = null,
            instrumentId = null,
            text = null
        )
    }
}
