package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.google.firebase.firestore.FirebaseFirestore

class ReadPlayersQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<List<FirestorePlayer>>(firebaseFirestore) {
    override fun execute() {
        firebaseFirestore
            .collection(playersCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val players: MutableList<FirestorePlayer> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val player = queryDocumentSnapshot.toObject(FirestorePlayer::class.java)
                    player.userId = queryDocumentSnapshot.id
                    players.add(player)
                }
                onSuccess(players.toList())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}