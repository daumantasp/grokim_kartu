package com.dauma.grokimkartu.repositories.thomanns.paginator

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannResponse
import com.dauma.grokimkartu.data.thomanns.entities.ThomannsResponse
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors
import com.dauma.grokimkartu.repositories.thomanns.ThomannsFilter
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUserConcise
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class ThomannsPaginator(
    private val playersDao: PlayersDao,
    private val user: User
    ) {
    protected var _pages: MutableStateFlow<List<ThomannsPage>> = MutableStateFlow(mutableListOf())
    val pages: StateFlow<List<ThomannsPage>> = _pages.asStateFlow()

    protected var _filter: MutableStateFlow<ThomannsFilter> = MutableStateFlow(ThomannsFilter.CLEAR)
    val filter: StateFlow<ThomannsFilter> = _filter.asStateFlow()

    protected val _isFilterApplied: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFilterApplied: StateFlow<Boolean> = _isFilterApplied.asStateFlow()

    val pageSize: Int = 20

    abstract suspend fun loadNextPage(): Result<ThomannsPage?, ThomannsErrors?>

    fun clear() {
        _pages.value = mutableListOf()
    }

    private fun setFilter(filter: ThomannsFilter) {
        _filter.update { filter }
        _isFilterApplied.update { filter.cityId != null
                || filter.validUntil != null
                || filter.isLocked != null
        }
        clear()
    }

    suspend fun setFilterAndReload(filter: ThomannsFilter): Result<ThomannsPage?, ThomannsErrors?> {
        setFilter(filter)
        return loadNextPage()
    }

    protected fun isLastLoaded(): Boolean {
        _pages.value.lastOrNull()?.let { pageValue ->
            return pageValue.isLast
        }
        return false
    }

    protected fun toThomannsPage(thomannsResponse: ThomannsResponse) : ThomannsPage {
        var thomanns: List<Thomann> = listOf()
        var isLastPage: Boolean = false

        if (thomannsResponse.data != null) {
            thomanns = thomannsResponse.data!!.map { tr ->
                val iconDownload: suspend ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                    val result = this.playersDao.playerIcon(tr.user?.id ?: -1, user.getBearerAccessToken()!!)
                    onComplete(result.data)
                }
                val icon = IconLoader(iconDownload)
                toThomann(tr, icon)
            }
        }
        if (thomannsResponse.pageData?.currentPage != null && thomannsResponse.pageData?.lastPage != null) {
            isLastPage = thomannsResponse.pageData?.currentPage == thomannsResponse.pageData?.lastPage
        }

        return ThomannsPage(thomanns, isLastPage)
    }

    protected fun toThomann(thomannResponse: ThomannResponse, icon: IconLoader): Thomann {
        val thomannUserConcise = ThomannUserConcise(
            id = thomannResponse.user?.id,
            name = thomannResponse.user?.name
        )
        return Thomann(
            id = thomannResponse.id,
            user = thomannUserConcise,
            city = thomannResponse.city,
            isOwner = thomannResponse.isOwner,
            isLocked = thomannResponse.isLocked,
            isAccessible = thomannResponse.isAccessible,
            isDeleted = thomannResponse.isDeleted,
            createdAt = thomannResponse.createdAt,
            validUntil = thomannResponse.validUntil,
            iconLoader = icon
        )
    }
}