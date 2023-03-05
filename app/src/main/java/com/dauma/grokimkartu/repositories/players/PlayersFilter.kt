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

    // NOTE: Values in state flow are conflated using Any.equals comparison in a similar way to
    // distinctUntilChanged operator.
    override fun equals(other: Any?): Boolean {
        if (other is PlayersFilter) {
            return cityId != other.cityId ||
                    instrumentId != other.instrumentId ||
                    text != other.text
        }
        return false
    }

    override fun hashCode(): Int {
        var result = cityId ?: 0
        result = 31 * result + (instrumentId ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        return result
    }
}
