package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.UpdateThomannQuery
import com.google.firebase.firestore.FirebaseFirestore

class UnlockThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                readThomann() { firestoreThomann, exception ->
                    if (firestoreThomann != null) {
                        val userWhoWantsToUnlockId = input!!
                        if (firestoreThomann.userId == userWhoWantsToUnlockId) {
                            if (firestoreThomann.locked == true) {
                                this.unlockThomann() { isSuccessful, exception ->
                                    if (isSuccessful) {
                                        onSuccess(null)
                                    } else {
                                        onFailure(exception)
                                    }
                                }
                            } else {
                                onFailure(Exception("Thomann is already unlocked"))
                            }
                        } else {
                            onFailure(Exception("Thomann can not be unlocked because user does not have rights"))
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

    private fun unlockThomann(onComplete: (Boolean, Exception?) -> Unit) {
        val lockedFirestoreThomann = FirestoreThomann(
            id = id!!,
            userId = null,
            name = null,
            city = null,
            locked = false,
            creationDate = null,
            validUntil = null,
            users = null
        )
        UpdateThomannQuery(firebaseFirestore)
            .withId(id!!)
            .withInput(lockedFirestoreThomann)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }
}