package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class DeletePlayerDetailsQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<Nothing>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(playerDetailsCollection)
                .document(id!!)
                .delete()
                .addOnSuccessListener { _ ->
                    this.onSuccess(null)
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}