package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDetailsResponse
import com.dauma.grokimkartu.data.thomanns.entities.ThomannResponse
import com.dauma.grokimkartu.data.thomanns.entities.ThomannsResponse
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.thomanns.entities.*
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginator
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

class MyThomannsRepositoryImpl(
    private val thomannsDao: ThomannsDao,
    private val playersDao: PlayersDao,
    private val paginator: ThomannsPaginator,
    private val user: User
) : MyThomannsRepository, LoginListener {
    private val _pages: MutableList<ThomannsPage> = mutableListOf()

    override val pages: List<ThomannsPage>
        get() = _pages

    override fun loadNextPage(onComplete: (ThomannsPage?, ThomannsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            paginator.loadNextPage(user.getBearerAccessToken()!!) { thomannsResponse, isLastPage ->
                if (thomannsResponse != null) {
                    val thomannsPage = toThomannsPage(thomannsResponse)
                    _pages.add(thomannsPage)
                    onComplete(thomannsPage, null)
                } else {
                    onComplete(null, ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun thomannDetails(
        thomannId: Int,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            thomannsDao.thomannDetails(thomannId, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannDetailsResponse != null) {
                    val thomannDetails = toThomannDetails(thomannDetailsResponse)
                    onComplete(thomannDetails, null)
                } else {
                    onComplete(null, ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun reload(onComplete: (ThomannsPage?, ThomannsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            reset()
            loadNextPage(onComplete)
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            _pages.clear()
            paginator.clear()
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toThomannsPage(thomannsResponse: ThomannsResponse) : ThomannsPage {
        var thomanns: List<Thomann> = listOf()
        var isLastPage: Boolean = false

        if (thomannsResponse.data != null) {
            thomanns = thomannsResponse.data!!.map { tr ->
                val iconDownload: ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                    this.playersDao.playerIcon(tr.user?.id ?: -1, user.getBearerAccessToken()!!) { icon, _ ->
                        onComplete(icon)
                    }
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

    private fun toThomann(thomannResponse: ThomannResponse, icon: IconLoader): Thomann {
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
            createdAt = thomannResponse.createdAt,
            validUntil = thomannResponse.validUntil,
            iconLoader = icon
        )
    }

    private fun toThomannDetails(thomannDetailsResponse: ThomannDetailsResponse): ThomannDetails {
        val thomannUsers = thomannDetailsResponse.users?.map { thomannUserResponse ->
            val iconDownload: ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                this.playersDao.playerIcon(thomannUserResponse.user?.id ?: -1, user.getBearerAccessToken()!!) { icon, _ ->
                    onComplete(icon)
                }
            }
            ThomannUser(
                id = thomannUserResponse.id,
                user = ThomannUserConcise(
                    id = thomannUserResponse.user?.id,
                    name = thomannUserResponse.user?.name
                ),
                amount = thomannUserResponse.amount,
                createdAt = thomannUserResponse.createdAt,
                isCurrentUser = thomannUserResponse.isCurrentUser,
                actions = thomannUserResponse.actions,
                iconLoader = IconLoader(iconDownload)
            )
        }
        return ThomannDetails(
            id = thomannDetailsResponse.id,
            user = ThomannUserConcise(
                id = thomannDetailsResponse.user?.id,
                name = thomannDetailsResponse.user?.name
            ),
            city = ThomannCity(
                id = thomannDetailsResponse.city?.id,
                name = thomannDetailsResponse.city?.name
            ),
            isOwner = thomannDetailsResponse.isOwner,
            isLocked = thomannDetailsResponse.isLocked,
            createdAt = thomannDetailsResponse.createdAt,
            validUntil = thomannDetailsResponse.validUntil,
            users = if (thomannUsers != null) ArrayList(thomannUsers) else null,
            totalAmount = thomannDetailsResponse.totalAmount,
            actions = thomannDetailsResponse.actions
        )
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            reset()
        }
    }
}