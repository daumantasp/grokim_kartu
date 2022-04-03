package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class DeletePlayerQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<Nothing>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(playersCollection)
                .document(id!!)
                .delete()
                .addOnSuccessListener { _ ->
                    onSuccess(null)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}