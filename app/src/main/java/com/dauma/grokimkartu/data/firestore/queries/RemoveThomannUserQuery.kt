package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RemoveThomannUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                readThomann { firestoreThomann, exception ->
                    if (firestoreThomann != null) {
                        val userId = input!!
                        val user = firestoreThomann.users?.firstOrNull { ftu -> ftu.userId == userId }
                        if (user != null) {
                            this.removeUser(user) { isSuccessful, exception ->
                                if (isSuccessful) {
                                    onSuccess(null)
                                } else {
                                    onFailure(exception)
                                }
                            }
                        } else {
                            onFailure(Exception("User can not be removed because he is not in the list"))
                        }
                    } else {
                        onFailure(exception)
                    }
                }
            } else {
                throw Exception("Input is not provided")
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

    private fun removeUser(
        user: FirestoreThomannUser,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(thomannsCollection)
            .document(id!!)
            .update("users", FieldValue.arrayRemove(user))
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}