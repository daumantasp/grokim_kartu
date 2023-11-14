package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.dauma.grokimkartu.data.instruments.InstrumentsDao
import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.entities.PlayerCity
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerInstrument
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator
import com.dauma.grokimkartu.repositories.profile.ProfileErrors
import com.dauma.grokimkartu.repositories.profile.ProfileException

class PlayersRepositoryImpl(
    private val playersDao: PlayersDao,
    override val paginator: PlayersPaginator,
    private val citiesDao: CitiesDao,
    private val instrumentsDao: InstrumentsDao,
    private val user: User
) : PlayersRepository {
    override suspend fun playerDetails(userId: Int): Result<PlayerDetails?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = playersDao.playerDetails(userId, user.getBearerAccessToken()!!)
            val status = response.status
            val playerDetailsResponse = response.data
            if (status.isSuccessful && playerDetailsResponse != null) {
                val playerDetails = PlayerDetails(
                    userId = playerDetailsResponse.userId.toString(),
                    name = playerDetailsResponse.name,
                    instrument = playerDetailsResponse.instrument,
                    description = playerDetailsResponse.description,
                    photo = null,
                    city = playerDetailsResponse.city
                )
                return Result(playerDetails, null)
            } else {
                return Result(null, PlayersErrors.UNKNOWN)
            }
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun playerPhoto(userId: Int): Result<Bitmap?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = playersDao.playerPhoto(userId, user.getBearerAccessToken()!!)
            val status = response.status
            val playerPhotoResponse = response.data
            if (status.isSuccessful && playerPhotoResponse != null) {
                return Result(playerPhotoResponse, null)
            } else {
                val error: PlayersErrors
                when (status.error) {
                    PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND -> {
                        error = PlayersErrors.PHOTO_NOT_FOUND
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

    override suspend fun reload(): Result<PlayersPage?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            reset()
            return paginator.loadNextPage()
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

    override suspend fun cities(): Result<List<PlayerCity>?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = citiesDao.cities(user.getBearerAccessToken()!!)
            val status = response.status
            val citiesResponse = response.data
            if (status.isSuccessful && citiesResponse != null) {
                val playerCities = citiesResponse.map { cr -> toPlayerCity(cr) }
                return Result(playerCities, null)
            } else {
                return Result(null, PlayersErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun searchCity(value: String): Result<List<PlayerCity>?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = citiesDao.search(value, user.getBearerAccessToken()!!)
            val status = response.status
            val citiesResponse = response.data
            if (status.isSuccessful && citiesResponse != null) {
                val playerCities = citiesResponse.map { cr -> toPlayerCity(cr) }
                return Result(playerCities, null)
            } else {
                return Result(null, PlayersErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun instruments(): Result<List<PlayerInstrument>?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = instrumentsDao.instruments(user.getBearerAccessToken()!!)
            val status = response.status
            val instrumentsResponse = response.data
            if (status.isSuccessful && instrumentsResponse != null) {
                val playerInstruments = instrumentsResponse.map { ir -> toPlayerInstrument(ir) }
                return Result(playerInstruments, null)
            } else {
                return Result(null, PlayersErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun searchInstrument(value: String): Result<List<PlayerInstrument>?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            val response = instrumentsDao.search(value, user.getBearerAccessToken()!!)
            val status = response.status
            val instrumentsResponse = response.data
            if (status.isSuccessful && instrumentsResponse != null) {
                val playerInstruments = instrumentsResponse.map { ir -> toPlayerInstrument(ir) }
                return Result(playerInstruments, null)
            } else {
                return Result(null, PlayersErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
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

    override fun loginCompleted(isSuccessful: Boolean) {
        if (isSuccessful) {
            reset()
        }
    }
}

