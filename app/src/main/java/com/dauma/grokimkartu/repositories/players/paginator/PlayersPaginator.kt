package com.dauma.grokimkartu.repositories.players.paginator

import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import com.dauma.grokimkartu.repositories.players.PlayersFilter

interface PlayersPaginator {
    val pages: List<PlayersResponse>
    val pageSize: Int
    var filter: PlayersFilter
    val isFilterApplied: Boolean
    fun loadNextPage(accessToken: String, onComplete: (PlayersResponse?, PlayersErrors?) -> Unit)
    fun clear()
}