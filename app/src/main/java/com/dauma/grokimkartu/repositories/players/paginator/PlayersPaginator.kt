package com.dauma.grokimkartu.repositories.players.paginator

import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.repositories.players.PlayersErrors

interface PlayersPaginator {
    val pages: List<PlayersResponse>
    val pageSize: Int
    var filter: PlayersPaginatorFilter
    val isFilterApplied: Boolean
    fun loadNextPage(accessToken: String, onComplete: (PlayersResponse?, PlayersErrors?) -> Unit)
    fun clear()
}