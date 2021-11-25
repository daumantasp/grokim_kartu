package com.dauma.grokimkartu.repositories.players

import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.entities.FirestorePlayer

class PlayersRepositoryImpl(private val playersDao: PlayersDao)
    : PlayersRepository {
    override fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, PlayersError?) -> Unit) {
        playersDao.getPlayers { isSuccessful, firestorePlayers, e ->
            if (isSuccessful && firestorePlayers != null) {
                onComplete(true, firestorePlayers, null)
            } else {
                onComplete(false, null, PlayersError(2))
            }
        }
    }

    override fun getPlayer(
        userId: String,
        onComplete: (Boolean, FirestorePlayer?, PlayersError?) -> Unit,
    ) {
        playersDao.getPlayer(userId) { isSuccessful, firestorePlayer, e ->
            if (isSuccessful && firestorePlayer != null) {
                onComplete(true, firestorePlayer, null)
            } else {
                // TODO
                onComplete(false, null, PlayersError(1))
            }
        }
    }

    override fun createPlayer(
        player: FirestorePlayer,
        onComplete: (Boolean, PlayersError?) -> Unit,
    ) {
        playersDao.setPlayer(player) { isSuccessful, e ->
            if (isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, PlayersError(2))
            }
        }
    }

    override fun updatePlayer(
        player: FirestorePlayer,
        onComplete: (Boolean, PlayersError?) -> Unit,
    ) {
        playersDao.setPlayer(player) { isSuccessful, e ->
            if (isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, PlayersError(2))
            }
        }
    }

    override fun deletePlayer(userId: String, onComplete: (Boolean, PlayersError?) -> Unit) {
        playersDao.deletePlayer(userId) { isSuccessful, e ->
            if (isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, PlayersError(2))
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