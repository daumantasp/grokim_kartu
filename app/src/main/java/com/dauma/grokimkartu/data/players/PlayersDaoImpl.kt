package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.firestore.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayerDetails
import com.dauma.grokimkartu.data.firestore.queries.players.ReadPlayerDetailsQuery
import com.dauma.grokimkartu.data.firestore.queries.players.ReadPlayersQuery
import com.dauma.grokimkartu.data.players.entities.PlayerDao
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsDao
import com.google.firebase.firestore.FirebaseFirestore

class PlayersDaoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : PlayersDao {

    override fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit) {
        ReadPlayersQuery(firebaseFirestore)
            .onSuccess { firestorePlayers ->
                val playersDaoList = firestorePlayers?.map { fp -> toPlayerDao(fp)!! }
                onComplete(true, playersDaoList, null)
            }
            .onFailure { exception ->
                onComplete(false, null, exception)
            }
            .execute()
    }

    override fun getPlayerPhoto(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        firebaseStorage.downloadProfilePhoto(userId, onComplete)
    }

    override fun getPlayerIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        firebaseStorage.downloadProfilePhotoIcon(userId, onComplete)
    }

    override fun getPlayerDetails(
        userId: String,
        onComplete: (PlayerDetailsDao?, Exception?) -> Unit
    ) {
        ReadPlayerDetailsQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestorePlayerDetails ->
                val playerDetailsDao = toPlayerDetailsDao(firestorePlayerDetails)
                onComplete(playerDetailsDao, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    private fun toPlayerDao(firestorePlayer: FirestorePlayer?) : PlayerDao? {
        var playerDao: PlayerDao? = null
        if (firestorePlayer != null) {
            playerDao = PlayerDao(
                firestorePlayer.userId,
                firestorePlayer.name,
                firestorePlayer.instrument,
                firestorePlayer.description,
                firestorePlayer.city
            )
        }
        return playerDao
    }

    private fun toPlayerDetailsDao(firestorePlayerDetails: FirestorePlayerDetails?) : PlayerDetailsDao? {
        var playerDetailsDao: PlayerDetailsDao? = null
        if (firestorePlayerDetails != null) {
            playerDetailsDao = PlayerDetailsDao(
                firestorePlayerDetails.userId,
                firestorePlayerDetails.name,
                firestorePlayerDetails.instrument,
                firestorePlayerDetails.description,
                firestorePlayerDetails.city
            )
        }
        return playerDetailsDao
    }
}