package com.dauma.grokimkartu.data.players

import com.dauma.grokimkartu.data.players.entities.FirestorePlayer
import com.google.firebase.firestore.FirebaseFirestore

class PlayersDaoImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : PlayersDao {

    companion object {
        private const val playersCollection = "players"
    }

    //https://firebase.google.com/docs/firestore/query-data/query-cursors#kotlin+ktx_3
    // TODO: implement pagination and filter only visible ones?
    //https://medium.com/firebase-tips-tricks/how-to-paginate-firestore-using-paging-3-on-android-c485acb0a2df
    override fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(PlayersDaoImpl.playersCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val players: MutableList<FirestorePlayer> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val player = queryDocumentSnapshot.toObject(FirestorePlayer::class.java)
                    players.add(player)
                }
                onComplete(true, players, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }

    override fun getPlayer(
        userId: String,
        onComplete: (Boolean, FirestorePlayer?, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(PlayersDaoImpl.playersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { playerDocumentSnapshot ->
                if (playerDocumentSnapshot.exists()) {
                    val player = playerDocumentSnapshot.toObject(FirestorePlayer::class.java)
                    onComplete(true, player, null)
                } else {
                    onComplete(false, null, Exception("PLAYER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }

    }

    //https://stackoverflow.com/questions/56608046/update-a-document-in-firestore
    override fun setPlayer(player: FirestorePlayer, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(PlayersDaoImpl.playersCollection)
            .document(player.userId)
            .set(player)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deletePlayer(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(PlayersDaoImpl.playersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}