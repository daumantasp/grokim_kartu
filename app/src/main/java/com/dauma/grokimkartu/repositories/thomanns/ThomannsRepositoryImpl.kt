package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDaoResponseStatus
import com.dauma.grokimkartu.data.thomanns.entities.*
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.thomanns.entities.*
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginator

class ThomannsRepositoryImpl(
    private val thomannsDao: ThomannsDao,
    private val playersDao: PlayersDao,
    private val citiesDao: CitiesDao,
    private val paginator: ThomannsPaginator,
    private val user: User
) : ThomannsRepository {
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

    override fun create(
        createThomann: CreateThomann,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val createThomannRequest = CreateThomannRequest(
                cityId = createThomann.cityId,
                validUntil = createThomann.validUntil
            )
            thomannsDao.create(createThomannRequest, user.getBearerAccessToken()!!) { thomannDetailsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannDetailsResponse != null) {
                    this.reset()
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
        updateThomann: UpdateThomann,
        onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val updateThomannRequest = UpdateThomannRequest(
                isLocked = updateThomann.isLocked,
                cityId = updateThomann.cityId,
                validUntil = updateThomann.validUntil
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
                    this.reset()
                    onComplete(null)
                } else {
                    onComplete(ThomannsErrors.UNKNOWN)
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

    override fun cities(onComplete: (List<ThomannCity>?, ThomannsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            citiesDao.cities(user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val thomannCities = citiesResponse.map { cr -> toThomannCity(cr) }
                    onComplete(thomannCities, null)
                } else {
                    onComplete(null, ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun searchCity(
        value: String,
        onComplete: (List<ThomannCity>?, ThomannsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            citiesDao.search(value, user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val thomannCities = citiesResponse.map { cr -> toThomannCity(cr) }
                    onComplete(thomannCities, null)
                } else {
                    onComplete(null, ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            throw ThomannsException(ThomannsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun reset() {
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
                val loader = { onComplete: (Bitmap?, ThomannsErrors?) -> Unit ->
                    this.playersDao.playerIcon(tr.user?.id ?: -1, user.getBearerAccessToken()!!) { icon, playersDaoResponseStatus ->
                        onComplete(icon, null)
                    }
                }
                val icon = ThomannPlayerIcon(loader)
                toThomann(tr, icon)
            }
        }
        if (thomannsResponse.pageData?.currentPage != null && thomannsResponse.pageData?.lastPage != null) {
            isLastPage = thomannsResponse.pageData?.currentPage == thomannsResponse.pageData?.lastPage
        }

        return ThomannsPage(thomanns, isLastPage)
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

    private fun toThomannCity(cityResponse: CityResponse): ThomannCity {
        return ThomannCity(
            id = cityResponse.id,
            name = cityResponse.name
        )
    }
}