package com.dauma.grokimkartu.repositories.players.paginator

import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import com.dauma.grokimkartu.repositories.players.PlayersFilter
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import kotlinx.coroutines.flow.StateFlow

interface PlayersPaginator {
    val pages: StateFlow<List<PlayersPage>>
    val filter: StateFlow<PlayersFilter>
    val isFilterApplied: StateFlow<Boolean>
    suspend fun loadNextPage(): Result<PlayersPage?, PlayersErrors?>
    suspend fun setFilterAndReload(filter: PlayersFilter): Result<PlayersPage?, PlayersErrors?>
    fun clear()
}