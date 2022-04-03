package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdatePlayerQuery(firestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestorePlayer>(firestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val playerToSet = getPlayersToSet(input!!)
                firebaseFirestore
                    .collection(playersCollection)
                    .document(id!!)
                    .set(playerToSet, SetOptions.merge())
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        this.onFailure(exception)
                    }
            } else {
                throw Exception("Player is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun getPlayersToSet(player: FirestorePlayer) : HashMap<String, Any> {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (player.name != null) {
            valuesToSet["name"] = player.name
        }
        if (player.instrument != null) {
            valuesToSet["instrument"] = player.instrument
        }
        if (player.description != null) {
            valuesToSet["description"] = player.description
        }
        if (player.city != null) {
            valuesToSet["city"] = player.city
        }
        return valuesToSet
    }
}