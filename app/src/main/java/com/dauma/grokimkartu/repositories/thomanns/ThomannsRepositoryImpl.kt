package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDaoResponseStatus
import com.dauma.grokimkartu.data.thomanns.entities.*
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.thomanns.entities.*

class ThomannsRepositoryImpl(
    private val thomannsDao: ThomannsDao,
    private val playersDao: PlayersDao,
    private val user: User
) : ThomannsRepository {

    override fun create(
        thomann: Thomann,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val createThomannRequest = CreateThomannRequest(validUntil = null)
            thomannsDao.create(createThomannRequest, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannDetailsResponse != null) {
                    val thomannDetails = toThomannDetails(thomannDetailsResponse)
                    onComplete(thomannDetails, null)
                } else {
                    when (thomannsDaoResponseStatus.error) {
                        ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE -> {
                            onComplete(null, ThomannsErrors.INVALID_VALID_UNTIL_DATE)
                        }
                        else -> {
                            onComplete(null, ThomannsErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun update(
        thomannId: Int,
        thomann: Thomann,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            // TODO: maybe pass UpdateThomannRequest to this method as a parameter
            val updateThomannRequest = UpdateThomannRequest(
                isLocked = thomann.isLocked,
                validUntil = null
            )
            thomannsDao.update(thomannId, updateThomannRequest, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannDetailsResponse != null) {
                    val thomannDetails = toThomannDetails(thomannDetailsResponse)
                    onComplete(thomannDetails, null)
                } else {
                    when (thomannsDaoResponseStatus.error) {
                        ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE -> {
                            onComplete(null, ThomannsErrors.INVALID_VALID_UNTIL_DATE)
                        }
                        else -> {
                            onComplete(null, ThomannsErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun delete(
        thomannId: Int,
        onComplete: (ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            thomannsDao.delete(thomannId, user.getBearerAccessToken()!!) { thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful) {
                    onComplete(null)
                } else {
                    onComplete(ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun thomanns(onComplete: (List<Thomann>?, ThomannsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            thomannsDao.thomanns(user.getBearerAccessToken()!!) { thomannsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannsResponse != null) {
                    val thomanns = thomannsResponse.map { td ->
                        val loader = { onComplete: (Bitmap?, ThomannsErrors?) -> Unit ->
                            this.playersDao.playerIcon(td.user?.id ?: -1, user.getBearerAccessToken()!!) { icon, playersDaoResponseStatus ->
                                onComplete(icon, null)
                            }
                        }
                        val thomannPlayerIcon = ThomannPlayerIcon(loader)
                        toThomann(td, thomannPlayerIcon)
                    }
                    onComplete(thomanns, null)
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

    override fun join(
        thomannId: Int,
        amount: Double,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val joinRequest = JoinThomannRequest(amount)
            thomannsDao.join(thomannId, joinRequest, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannDetailsResponse != null) {
                    val thomannDetails = toThomannDetails(thomannDetailsResponse)
                    onComplete(thomannDetails, null)
                } else {
                    when (thomannsDaoResponseStatus.error) {
                        ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE -> {
                            onComplete(null, ThomannsErrors.INVALID_VALID_UNTIL_DATE)
                        }
                        ThomannsDaoResponseStatus.Errors.INVALID_AMOUNT -> {
                            onComplete(null, ThomannsErrors.INVALID_AMOUNT)
                        }
                        else -> {
                            onComplete(null, ThomannsErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun quit(
        thomannId: Int,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            thomannsDao.quit(thomannId, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
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

    override fun kick(
        thomannId: Int,
        userToKickId: Int,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val kickRequest = KickThomannRequest(userToKickId)
            thomannsDao.kick(thomannId, kickRequest, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
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

    private fun toThomann(thomannResponse: ThomannResponse, icon: ThomannPlayerIcon): Thomann {
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
            icon = icon
        )
    }

    private fun toThomannDetails(thomannDetailsResponse: ThomannDetailsResponse): ThomannDetails {
        val thomannUsers = thomannDetailsResponse.users?.map { thomannUserResponse ->
            val loader = { onComplete: (Bitmap?, ThomannsErrors?) -> Unit ->
                this.playersDao.playerIcon(thomannUserResponse.user?.id ?: -1, user.getBearerAccessToken()!!) { icon, _ ->
                    onComplete(icon, null)
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
                icon = ThomannPlayerIcon(loader)
            )
        }
        return ThomannDetails(
            id = thomannDetailsResponse.id,
            user = ThomannUserConcise(
                id = thomannDetailsResponse.user?.id,
                name = thomannDetailsResponse.user?.name
            ),
            city = thomannDetailsResponse.city,
            isOwner = thomannDetailsResponse.isOwner,
            isLocked = thomannDetailsResponse.isLocked,
            createdAt = thomannDetailsResponse.createdAt,
            validUntil = thomannDetailsResponse.validUntil,
            users = if (thomannUsers != null) ArrayList(thomannUsers) else null,
            totalAmount = thomannDetailsResponse.totalAmount,
            actions = thomannDetailsResponse.actions
        )
    }
}