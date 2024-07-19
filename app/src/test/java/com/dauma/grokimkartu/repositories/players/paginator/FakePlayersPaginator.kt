package com.dauma.grokimkartu.repositories.players.paginator

import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import com.dauma.grokimkartu.repositories.players.PlayersFilter
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlayersPaginator(
    private val initialPlayersData: List<Player>
) : PlayersPaginator {

    private var _pages: MutableStateFlow<List<PlayersPage>> = MutableStateFlow(mutableListOf())
    override val pages: StateFlow<List<PlayersPage>> = _pages.asStateFlow()

    private var _filter: MutableStateFlow<PlayersFilter> = MutableStateFlow(PlayersFilter.CLEAR)
    override var filter: StateFlow<PlayersFilter> = _filter.asStateFlow()

    private val _isFilterApplied: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isFilterApplied: StateFlow<Boolean> = _isFilterApplied.asStateFlow()

    override suspend fun loadNextPage(): Result<PlayersPage?, PlayersErrors?> {
        if (pages.value.isEmpty()) {
            val playersPage = PlayersPage(initialPlayersData, true)
            val pages = _pages.value.toMutableList()
            pages.add(playersPage)
            _pages.value = pages
            return Result(playersPage, null)
        } else {
            return Result(_pages.value.lastOrNull(), null)
        }
    }

    override suspend fun setFilterAndReload(filter: PlayersFilter): Result<PlayersPage?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}