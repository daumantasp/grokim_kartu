package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerIcon

class PlayersRepositoryImpl(
    private val playersDao: PlayersDao,
    private val user: User
) : PlayersRepository {
    override fun players(onComplete: (List<Player>?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            playersDao.players(user.getBearerAccessToken()!!) { playersResponse, playersDaoResponseStatus ->
                if (playersDaoResponseStatus.isSuccessful && playersResponse != null) {
                    val players = playersResponse.map { pr ->
                        val loader = { onComplete: (Bitmap?, PlayersErrors?) -> Unit ->
                            this.playerIcon(pr.id ?: -1, onComplete)
                        }
                        Player(
                            userId = pr.id,
                            name = pr.name,
                            instrument = pr.instrument,
                            description = "",
                            icon = PlayerIcon(loader),
                            city = pr.city
                        )
                    }
                    onComplete(players, null)
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
                        userId = playerDetailsResponse.id.toString(),
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
}