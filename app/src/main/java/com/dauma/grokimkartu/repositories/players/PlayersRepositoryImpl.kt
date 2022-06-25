package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.notifications.NotificationsErrors
import com.dauma.grokimkartu.repositories.notifications.NotificationsException
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerIcon
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

class PlayersRepositoryImpl(
    private val playersDao: PlayersDao,
    private val paginator: PlayersPaginator,
    private val user: User
) : PlayersRepository, LoginListener {
    private val _playersPages: MutableList<PlayersPage> = mutableListOf()

    override val pages: List<PlayersPage>
        get() = _playersPages

    override fun loadNextPage(onComplete: (PlayersPage?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            paginator.loadNextPage(user.getBearerAccessToken()!!) { playersResponse, isLastPage ->
                if (playersResponse != null) {
                    val playersPage = toPlayersPage(playersResponse)
                    _playersPages.add(playersPage)
                    onComplete(playersPage, null)
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
            _playersPages.clear()
            paginator.clear()
        } else {
            throw PlayersException(PlayersErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toPlayersPage(playersResponse: PlayersResponse) : PlayersPage {
        var players: List<Player> = listOf()
        var isLastPage: Boolean = false

        if (playersResponse.data != null) {
            players = playersResponse.data!!.map { pr ->
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
        }
        if (playersResponse.pageData?.currentPage != null && playersResponse.pageData?.lastPage != null) {
            isLastPage = playersResponse.pageData?.currentPage == playersResponse.pageData?.lastPage
        }

        return PlayersPage(players, isLastPage)
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            reset()
        }
    }
}

