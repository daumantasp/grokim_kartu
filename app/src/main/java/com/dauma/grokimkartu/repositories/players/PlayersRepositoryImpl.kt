package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.repositories.players.entities.Player

class PlayersRepositoryImpl(private val playersDao: PlayersDao) : PlayersRepository {
    override fun getPlayers(onComplete: (Boolean, List<Player>?, PlayersError?) -> Unit) {
        playersDao.getPlayers { isSuccessful, playersDao, e ->
            if (isSuccessful && playersDao != null) {
                val players = playersDao.map { pd -> Player(
                    pd.userId,
                    pd.name,
                    pd.instrument,
                    pd.description
                ) }
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