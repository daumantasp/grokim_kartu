package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerIcon

class PlayersRepositoryImpl(private val playersDao: PlayersDao) : PlayersRepository {
    override fun getPlayers(onComplete: (Boolean, List<Player>?, PlayersError?) -> Unit) {
        playersDao.getPlayers { isSuccessful, playersDao, e ->
            if (isSuccessful && playersDao != null) {
                val players = playersDao.map { pd ->
                    val loader = { onComplete: (Bitmap?, PlayersError?) -> Unit ->
                        this.getPlayerIcon(pd.userId ?: "", onComplete)
                    }
                    Player(
                        pd.userId,
                        pd.name,
                        pd.instrument,
                        pd.description,
                        PlayerIcon(loader),
                        pd.city
                    )
                }
                onComplete(true, players, null)
            } else {
                onComplete(false, null, PlayersError(2))
            }
        }
    }

    override fun getPlayerIcon(userId: String, onComplete: (Bitmap?, PlayersError?) -> Unit) {
        playersDao.getPlayerIcon(userId) { playerIcon, e ->
            if (playerIcon != null) {
                onComplete(playerIcon, null)
            } else {
                onComplete(null, PlayersError(2))
            }
        }
    }

    override fun getPlayerPhoto(userId: String, onComplete: (Bitmap?, PlayersError?) -> Unit) {
        playersDao.getPlayerPhoto(userId) { playerPhoto, e ->
            if (playerPhoto != null) {
                onComplete(playerPhoto, null)
            } else {
                onComplete(null, PlayersError(2))
            }
        }
    }

    override fun getPlayerDetails(
        userId: String,
        onComplete: (PlayerDetails?, PlayersError?) -> Unit
    ) {
        playersDao.getPlayerDetails(userId) { playerDetailsDao, e ->
            if (playerDetailsDao != null) {
                val playerDetails = PlayerDetails(
                    playerDetailsDao.userId,
                    playerDetailsDao.name,
                    playerDetailsDao.instrument,
                    playerDetailsDao.description,
                    null,
                    playerDetailsDao.city
                )
                onComplete(playerDetails, null)
            } else {
                onComplete(null, PlayersError(2))
            }
        }
    }
}

class PlayersException(error: PlayersError)
    : Exception(error.message) {}

class PlayersError(val code: Int) {
    val message: String = when(code) {
        1 -> PLAYER_NOT_FOUND
        2 -> SOMETHING_FAILED
        else -> ""
    }

    companion object {
        const val PLAYER_NOT_FOUND = "Player was not found!"
        const val SOMETHING_FAILED = "Something failed"
    }
}