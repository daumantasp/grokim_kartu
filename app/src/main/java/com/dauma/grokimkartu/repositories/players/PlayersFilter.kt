package com.dauma.grokimkartu.repositories.players

class PlayersFilter(
    val cityId: Int?,
    val instrumentId: Int?,
    val text: String?) {
    constructor() : this(
        cityId = null,
        instrumentId = null,
        text = null
    )

    constructor(filter: PlayersFilter) : this(
        cityId = filter.cityId,
        instrumentId = filter.instrumentId,
        text = filter.text
    )
}
