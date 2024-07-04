package com.dauma.grokimkartu.repositories.players.paginator

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.data.players.entities.PlayersRequest
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import com.dauma.grokimkartu.repositories.players.PlayersException
import com.dauma.grokimkartu.repositories.players.PlayersFilter
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayersPaginatorImpl(
    private val playersDao: PlayersDao,
    private val user: User
    ) : PlayersPaginator {
    private var _pages: MutableStateFlow<List<PlayersPage>> = MutableStateFlow(mutableListOf())
    override val pages: StateFlow<List<PlayersPage>> = _pages.asStateFlow()

    private var _filter: MutableStateFlow<PlayersFilter> = MutableStateFlow(PlayersFilter.CLEAR)
    override var filter: StateFlow<PlayersFilter> = _filter.asStateFlow()

    private val _isFilterApplied: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isFilterApplied: StateFlow<Boolean> = _isFilterApplied.asStateFlow()

    private val pageSize: Int = 20

    override suspend fun loadNextPage(): Result<PlayersPage?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.value.count() + 1
                val playersRequest = PlayersRequest(
                    page = nextPage,
                    pageSize = pageSize,
                    cityId = _filter.value.cityId,
                    instrumentId = _filter.value.instrumentId,
                    text = _filter.value.text
                )
                val response = playersDao.players(playersRequest, user.getBearerAccessToken()!!)
                val status = response.status
                val playersResponse = response.data
                if (status.isSuccessful && playersResponse != null) {
                    val playersPage = toPlayersPage(playersResponse)
                    val pages = _pages.value.toMutableList()
                    pages.add(playersPage)
                    _pages.value = pages
                    return Result(playersPage, null)
                } else {
                    return Result(null, PlayersErrors.UNKNOWN)
                }
            } else {
                return Result(_pages.value.lastOrNull(), null)
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun isLastLoaded(): Boolean {
        _pages.value.lastOrNull()?.let { pageValue ->
            return pageValue.isLast
        }
        return false
    }

    private fun toPlayersPage(playersResponse: PlayersResponse) : PlayersPage {
        var players: List<Player> = listOf()
        var isLastPage: Boolean = false

        if (playersResponse.data != null) {
            players = playersResponse.data!!.map { pr ->
                val iconDownload: suspend ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                    val result = playerIcon(pr.userId ?: -1)
                    onComplete(result.data)
                }
                Player(
                    userId = pr.userId,
                    name = pr.name,
                    instrument = pr.instrument,
                    description = "",
                    iconLoader = IconLoader(iconDownload),
                    city = pr.city
                )
            }
        }
        if (playersResponse.pageData?.currentPage != null && playersResponse.pageData?.lastPage != null) {
            isLastPage = playersResponse.pageData?.currentPage == playersResponse.pageData?.lastPage
        }

        return PlayersPage(players, isLastPage)
    }

    private suspend fun playerIcon(userId: Int): Result<Bitmap?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = playersDao.playerIcon(userId, user.getBearerAccessToken()!!)
            val status = response.status
            val playerIconResponse = response.data
            if (status.isSuccessful && playerIconResponse != null) {
                return Result(playerIconResponse, null)
            } else {
                val error: PlayersErrors
                when (status.error) {
                    PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                        error = PlayersErrors.ICON_NOT_FOUND
                    }
                    else -> {
                        error = PlayersErrors.UNKNOWN
                    }
                }
                return Result(null, error)
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun setFilterAndReload(filter: PlayersFilter): Result<PlayersPage?, PlayersErrors?> {
        setFilter(filter)
        return loadNextPage()
    }

    private fun setFilter(filter: PlayersFilter) {
        _filter.update { filter }
        _isFilterApplied.update { filter.cityId != null
                || filter.instrumentId != null
                || filter.text != null
        }
        clear()
    }

    override fun clear() {
        _pages.value = mutableListOf()
    }
}