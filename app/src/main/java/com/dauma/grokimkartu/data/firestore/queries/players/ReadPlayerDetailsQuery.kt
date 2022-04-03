package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayerDetails
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class ReadPlayerDetailsQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<FirestorePlayerDetails>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(playerDetailsCollection)
                .document(id!!)
                .get()
                .addOnSuccessListener { playerDetailsDocumentSnapshot ->
                    if (playerDetailsDocumentSnapshot.exists()) {
                        val playerDetails = playerDetailsDocumentSnapshot.toObject(FirestorePlayerDetails::class.java)
                        playerDetails?.userId = id!!
                        this.onSuccess(playerDetails)
                    } else {
                        this.onFailure(Exception("Player details was not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}