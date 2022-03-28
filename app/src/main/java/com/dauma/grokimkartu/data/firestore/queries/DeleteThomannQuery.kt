package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.google.firebase.firestore.FirebaseFirestore

class DeleteThomannQuery(
    firebaseFirestore: FirebaseFirestore,
    private val readThomannQuery: FirestoreQuery<FirestoreThomann>
) : FirestoreInputQuery<Nothing, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val userWhoInitDeletionId = input!!
                readThomannQuery
                    .withId(id!!)
                    .onSuccess { firestoreThomann ->
                        if (firestoreThomann?.userId == userWhoInitDeletionId) {
                            firebaseFirestore
                                .collection(thomannsCollection)
                                .document(id!!)
                                .delete()
                                .addOnSuccessListener { _ ->
                                    onSuccess(null)
                                }
                                .addOnFailureListener { exception ->
                                    onFailure(exception)
                                }
                        } else {
                            onFailure(Exception("User does not have rights to delete Thomann"))
                        }
                    }
                    .onFailure { exception ->
                        onFailure(exception)
                    }
                    .execute()
            } else {
                throw Exception("Input is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }
}