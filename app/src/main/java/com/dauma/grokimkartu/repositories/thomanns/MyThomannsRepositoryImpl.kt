package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDetailsResponse
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.thomanns.entities.*
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginator
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

class MyThomannsRepositoryImpl(
    private val thomannsDao: ThomannsDao,
    private val playersDao: PlayersDao,
    override val paginator: ThomannsPaginator,
    private val user: User
) : MyThomannsRepository, LoginListener {
    override suspend fun thomannDetails(thomannId: Int): Result<ThomannDetails?, ThomannsErrors?> {
        if (user.isUserLoggedIn()) {
            val response = thomannsDao.thomannDetails(thomannId, user.getBearerAccessToken()!!)
            val status = response.status
            val thomannDetailsResponse = response.data
            if (status.isSuccessful && thomannDetailsResponse != null) {
                val thomannDetails = toThomannDetails(thomannDetailsResponse)
                return Result(thomannDetails, null)
            } else {
                return Result(null, ThomannsErrors.UNKNOWN)
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun reload(): Result<ThomannsPage?, ThomannsErrors?> {
        if (user.isUserLoggedIn()) {
            reset()
            return paginator.loadNextPage()
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            paginator.clear()
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toThomannDetails(thomannDetailsResponse: ThomannDetailsResponse): ThomannDetails {
        val thomannUsers = thomannDetailsResponse.users?.map { thomannUserResponse ->
            val iconDownload: suspend ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                val response = playersDao.playerIcon(thomannUserResponse.user?.id ?: -1, user.getBearerAccessToken()!!)
                onComplete(response.data)
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