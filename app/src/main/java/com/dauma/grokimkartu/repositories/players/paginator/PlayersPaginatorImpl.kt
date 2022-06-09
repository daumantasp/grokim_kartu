package com.dauma.grokimkartu.repositories.players.paginator

import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.repositories.players.PlayersErrors

class PlayersPaginatorImpl(private val playersDao: PlayersDao) : PlayersPaginator {
    private var _pages: MutableList<PlayersResponse> = mutableListOf()
    override val pages: List<PlayersResponse>
        get() = _pages
    override val pageSize: Int = 20

    override fun loadNextPage(accessToken: String, onComplete: (PlayersResponse?, PlayersErrors?) -> Unit) {
        if (isLastLoaded() == false) {
            val nextPage = _pages.count() + 1
            playersDao.players(nextPage, pageSize, accessToken) { playersResponse, playersDaoResponseStatus ->
                if (playersDaoResponseStatus.isSuccessful && playersResponse != null) {
                    _pages.add(playersResponse)
                    onComplete(playersResponse, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            onComplete(_pages.lastOrNull(), null)
        }
    }

    override fun clear() {
        _pages.clear()
    }

    private fun isLastLoaded(): Boolean {
        _pages.lastOrNull()?.pageData?.let { pageData ->
            return pageData.currentPage == pageData.lastPage
        }
        return false
    }
}