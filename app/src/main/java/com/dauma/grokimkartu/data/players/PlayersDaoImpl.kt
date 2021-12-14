package com.dauma.grokimkartu.data.players

import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.players.entities.PlayerDao

class PlayersDaoImpl(
    private val firestore: Firestore,
) : PlayersDao {

    override fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit) {
        firestore.getPlayers() { isSuccessful, firestorePlayersList, e ->
            val playersDaoList = firestorePlayersList?.map { fp -> toPlayerDao(fp)!! }
            onComplete(isSuccessful, playersDaoList, e)
        }
    }

    fun toPlayerDao(firestorePlayer: FirestorePlayer?) : PlayerDao? {
        var playerDao: PlayerDao? = null
        if (firestorePlayer != null) {
            playerDao = PlayerDao(
                firestorePlayer.userId,
                firestorePlayer.name,
                firestorePlayer.instrument,
                firestorePlayer.description
            )
        }
        return playerDao
    }
}