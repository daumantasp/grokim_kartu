package com.dauma.grokimkartu.data.firestore

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannActions
import com.dauma.grokimkartu.data.firestore.queries.*
import com.dauma.grokimkartu.data.firestore.queries.composite.DeleteThomannQuery
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : Firestore {
    companion object {
        private const val thomannsCollection = "thomanns"
    }

    private fun updateThomann(
        thomann: FirestoreThomann,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        UpdateThomannQuery(firebaseFirestore)
            .withId(thomann.id ?: "")
            .withInput(thomann)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun getThomann(thomannId: String, onComplete: (FirestoreThomann?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(thomannId)
            .onSuccess { firestoreThomann ->
                onComplete(firestoreThomann, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun lockThomann(
        thomannId: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        getThomann(thomannId) { firestoreThomann, exception ->
            if (firestoreThomann != null) {
                if (firestoreThomann.userId == userId) {
                    val lockedFirestoreThomann = FirestoreThomann(
                        id = thomannId,
                        userId = null,
                        name = null,
                        city = null,
                        locked = true,
                        creationDate = null,
                        validUntil = null,
                        users = null
                    )
                    this.updateThomann(lockedFirestoreThomann) { isSuccessful, e ->
                        if (isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, e)
                        }
                    }
                } else {
                    onComplete(false, Exception("THOMANN CANT BE LOCKED BECAUSE USER IDS DO NOT MATCH"))
                }
            } else {
                onComplete(false, exception)
            }
        }
    }

    override fun unlockThomann(
        thomannId: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        getThomann(thomannId) { firestoreThomann, exception ->
            if (firestoreThomann != null) {
                if (firestoreThomann.userId == userId) {
                    val lockedFirestoreThomann = FirestoreThomann(
                        id = thomannId,
                        userId = null,
                        name = null,
                        city = null,
                        locked = false,
                        creationDate = null,
                        validUntil = null,
                        users = null
                    )
                    this.updateThomann(lockedFirestoreThomann) { isSuccessful, e ->
                        if (isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, e)
                        }
                    }
                } else {
                    onComplete(false, Exception("THOMANN CANT BE UNLOCKED BECAUSE USER IDS DO NOT MATCH"))
                }
            } else {
                onComplete(false, exception)
            }
        }
    }
}