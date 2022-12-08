package com.dauma.grokimkartu.repositories.players.paginator

class PlayersPaginatorFilter(
    val cityId: Int?,
    val instrumentId: Int?,
    val text: String?) {
    constructor() : this(
        cityId = null,
        instrumentId = null,
        text = null
    )

    constructor(filter: PlayersPaginatorFilter) : this(
        cityId = filter.cityId,
        instrumentId = filter.instrumentId,
        text = filter.text
    )
}
