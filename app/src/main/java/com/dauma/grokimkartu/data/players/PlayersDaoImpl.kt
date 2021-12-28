package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.firestore.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.players.entities.PlayerDao

class PlayersDaoImpl(
    private val firestore: Firestore,
    private val firebaseStorage: FirebaseStorage
) : PlayersDao {

    override fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit) {
        firestore.getPlayers() { isSuccessful, firestorePlayersList, e ->
            val playersDaoList = firestorePlayersList?.map { fp -> toPlayerDao(fp)!! }
            onComplete(isSuccessful, playersDaoList, e)
        }
    }

    override fun getPlayerIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        firebaseStorage.downloadProfilePhotoIcon(userId, onComplete)
    }

    fun toPlayerDao(firestorePlayer: FirestorePlayer?) : PlayerDao? {
        var playerDao: PlayerDao? = null
        if (firestorePlayer != null) {
            playerDao = PlayerDao(
                firestorePlayer.userId,
                firestorePlayer.name,
                firestorePlayer.instrument,
                firestorePlayer.description,
                null
            )
        }
        return playerDao
    }
}