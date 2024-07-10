package com.dauma.grokimkartu.repositories.thomanns.paginator

import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors
import com.dauma.grokimkartu.repositories.thomanns.ThomannsException
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage

class MyThomannsPaginatorImpl(
    private val thomannsDao: ThomannsDao,
    playersDao: PlayersDao,
    private val user: User
) : ThomannsPaginator(playersDao, user) {
    override suspend fun loadNextPage(): Result<ThomannsPage?, ThomannsErrors?> {
        if (user.isUserLoggedIn()) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.value.count() + 1
                val response = thomannsDao.myThomanns(nextPage, pageSize, user.getBearerAccessToken()!!)
                val status = response.status
                val thomannsResponse = response.data
                if (status.isSuccessful && thomannsResponse != null) {
                    val thomannsPage = toThomannsPage(thomannsResponse)
                    val pages = _pages.value.toMutableList()
                    pages.add(thomannsPage)
                    _pages.value = pages
                    return Result(thomannsPage, null)
                } else {
                    return Result(null, ThomannsErrors.UNKNOWN)
                }
            } else {
                return Result(_pages.value.lastOrNull(), null)
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }
}