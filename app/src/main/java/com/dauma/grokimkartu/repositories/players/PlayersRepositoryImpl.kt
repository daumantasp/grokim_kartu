package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.dauma.grokimkartu.data.instruments.InstrumentsDao
import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.players.entities.*
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator
import com.dauma.grokimkartu.repositories.profile.ProfileErrors
import com.dauma.grokimkartu.repositories.profile.ProfileException
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

class PlayersRepositoryImpl(
    private val playersDao: PlayersDao,
    private val paginator: PlayersPaginator,
    private val citiesDao: CitiesDao,
    private val instrumentsDao: InstrumentsDao,
    private val user: User
) : PlayersRepository, LoginListener {
    override val pages: List<PlayersPage>
        get() = paginator.pages.map { pr -> toPlayersPage(pr) }

    override var filter: PlayersFilter
        get() = paginator.filter
        set(value) {
            paginator.filter = value
        }

    override val isFilterApplied: Boolean
        get() = paginator.isFilterApplied

    override fun loadNextPage(onComplete: (PlayersPage?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            paginator.loadNextPage(user.getBearerAccessToken()!!) { playersResponse, isLastPage ->
                if (playersResponse != null) {
                    onComplete(toPlayersPage(playersResponse), null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun playerDetails(
        userId: Int,
        onComplete: (PlayerDetails?, PlayersErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            playersDao.playerDetails(userId, user.getBearerAccessToken()!!) { playerDetailsResponse, playersDaoResponseStatus ->
                if (playersDaoResponseStatus.isSuccessful && playerDetailsResponse != null) {
                    val playerDetails = PlayerDetails(
                        userId = playerDetailsResponse.userId.toString(),
                        name = playerDetailsResponse.name,
                        instrument = playerDetailsResponse.instrument,
                        description = playerDetailsResponse.description,
                        photo = null,
                        city = playerDetailsResponse.city
                    )
                    onComplete(playerDetails, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun playerPhoto(
        userId: Int,
        onComplete: (Bitmap?, PlayersErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            playersDao.playerPhoto(userId, user.getBearerAccessToken()!!) { playerPhoto, playersDaoResponseStatus ->
                if (playerPhoto != null) {
                    onComplete(playerPhoto, null)
                } else {
                    val error: PlayersErrors
                    when (playersDaoResponseStatus.error) {
                        PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND -> {
                            error = PlayersErrors.PHOTO_NOT_FOUND
                        }
                        else -> {
                            error = PlayersErrors.UNKNOWN
                        }
                    }
                    onComplete(null, error)
                }
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun playerIcon(
        userId: Int,
        onComplete: (Bitmap?, PlayersErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            playersDao.playerIcon(userId, user.getBearerAccessToken()!!) { playerIcon, playersDaoResponseStatus ->
                if (playerIcon != null) {
                    onComplete(playerIcon, null)
                } else {
                    val error: PlayersErrors
                    when (playersDaoResponseStatus.error) {
                        PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                            error = PlayersErrors.ICON_NOT_FOUND
                        }
                        else -> {
                            error = PlayersErrors.UNKNOWN
                        }
                    }
                    onComplete(null, error)
                }
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun reload(onComplete: (PlayersPage?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            reset()
            loadNextPage(onComplete)
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            paginator.clear()
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun cities(onComplete: (List<PlayerCity>?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            citiesDao.cities(user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val playerCities = citiesResponse.map { cr -> toPlayerCity(cr) }
                    onComplete(playerCities, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun searchCity(
        value: String,
        onComplete: (List<PlayerCity>?, PlayersErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            citiesDao.search(value, user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val playerCities = citiesResponse.map { cr -> toPlayerCity(cr) }
                    onComplete(playerCities, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun instruments(onComplete: (List<PlayerInstrument>?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            instrumentsDao.instruments(user.getBearerAccessToken()!!) { instrumentsResponse, instrumentsDaoResponseStatus ->
                if (instrumentsDaoResponseStatus.isSuccessful && instrumentsResponse != null) {
                    val playerInstruments = instrumentsResponse.map { ir -> toPlayerInstrument(ir) }
                    onComplete(playerInstruments, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun searchInstrument(
        value: String,
        onComplete: (List<PlayerInstrument>?, PlayersErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            instrumentsDao.search(value, user.getBearerAccessToken()!!) { instrumentsResponse, instrumentsDaoResponseStatus ->
                if (instrumentsDaoResponseStatus.isSuccessful && instrumentsResponse != null) {
                    val playerInstruments = instrumentsResponse.map { ir -> toPlayerInstrument(ir) }
                    onComplete(playerInstruments, null)
                } else {
                    onComplete(null, PlayersErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toPlayersPage(playersResponse: PlayersResponse) : PlayersPage {
        var players: List<Player> = listOf()
        var isLastPage: Boolean = false

        if (playersResponse.data != null) {
            players = playersResponse.data!!.map { pr ->
                val iconDownload: ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
                    this.playerIcon(pr.userId ?: -1) { icon, _ ->
                        onComplete(icon)
                    }
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

    private fun toPlayerCity(cityResponse: CityResponse): PlayerCity {
        return PlayerCity(
            id = cityResponse.id,
            name = cityResponse.name
        )
    }

    private fun toPlayerInstrument(instrumentResponse: InstrumentResponse): PlayerInstrument {
        return PlayerInstrument(
            id = instrumentResponse.id,
            name = instrumentResponse.name
        )
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            reset()
        }
    }
}

