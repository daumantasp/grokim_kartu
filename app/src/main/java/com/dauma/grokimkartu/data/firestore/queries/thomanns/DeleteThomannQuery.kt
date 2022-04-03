package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore

class DeleteThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val userWhoInitDeletionId = input!!
                readThomann() { firestoreThomann, exception ->
                    if (firestoreThomann != null) {
                        if (firestoreThomann.userId == userWhoInitDeletionId) {
                            this.deleteThomann() { isSuccessful, exception ->
                                if (isSuccessful) {
                                    this.onSuccess(null)
                                } else {
                                    this.onFailure(exception)
                                }
                            }
                        } else {
                            this.onFailure(Exception("User does not have rights to delete Thomann"))
                        }
                    } else {
                        this.onFailure(exception)
                    }
                }
            } else {
                throw Exception("User id is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }

    private fun readThomann(onComplete: (FirestoreThomann?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreThomann ->
                onComplete(firestoreThomann, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    private fun deleteThomann(onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(thomannsCollection)
            .document(id!!)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception)
            }
    }
}