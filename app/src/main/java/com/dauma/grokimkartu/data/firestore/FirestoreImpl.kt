package com.dauma.grokimkartu.data.firestore

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannActions
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.firestore.queries.*
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

    override fun deleteThomann(thomannId: String, userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        val readThomannQuery = ReadThomannQuery(firebaseFirestore)
        DeleteThomannQuery(firebaseFirestore, readThomannQuery)
            .withId(thomannId)
            .withInput(userId)
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

    override fun getThomannActions(
        thomannId: String,
        userId: String,
        onComplete: (FirestoreThomannActions?, Exception?) -> Unit
    ) {
        getThomann(thomannId) { firestoreThomann, e ->
            if (firestoreThomann != null) {
                if (firestoreThomann.userId == userId) {
                    val actions = FirestoreThomannActions(
                        thomannId = thomannId,
                        isAccessible = true,
                        isJoinable = false,
                        isUpdatable = true
                    )
                    onComplete(actions, null)
                } else if (firestoreThomann.locked == false) {
                    val actions = FirestoreThomannActions(
                        thomannId = thomannId,
                        isAccessible = true,
                        isJoinable = true,
                        isUpdatable = false
                    )
                    onComplete(actions, null)
                } else {
                    val users = firestoreThomann.users
                    if (users != null) {
                        val user = users.firstOrNull { ftu -> ftu.userId == userId }
                        if (user != null) {
                            val actions = FirestoreThomannActions(
                                thomannId = thomannId,
                                isAccessible = true,
                                isJoinable = true,
                                isUpdatable = false
                            )
                            onComplete(actions, null)
                        } else {
                            val actions = FirestoreThomannActions(
                                thomannId = thomannId,
                                isAccessible = false,
                                isJoinable = false,
                                isUpdatable = false
                            )
                            onComplete(actions, null)
                        }
                    } else {
                        val actions = FirestoreThomannActions(
                            thomannId = thomannId,
                            isAccessible = false,
                            isJoinable = false,
                            isUpdatable = false
                        )
                        onComplete(actions, null)
                    }
                }
            } else {
                onComplete(null, e)
            }
        }
    }

    override fun leaveThomann(
        thomannId: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        getThomann(thomannId) { firestoreThomann, e ->
            if (firestoreThomann != null) {
                val users = firestoreThomann.users
                if (users != null) {
                    val user = users.firstOrNull { ftu -> ftu.userId == userId }
                    if (user != null) {
                        DeleteThomannUserQuery(firebaseFirestore)
                            .withId(thomannId)
                            .withInput(user)
                            .onSuccess { _ ->
                                onComplete(true, null)
                            }
                            .onFailure { exception ->
                                onComplete(false, exception)
                            }
                            .execute()
                    } else {
                        onComplete(false, Exception("THOMANN USER WITH ID=$userId NOT FOUND"))
                    }
                } else {
                    onComplete(false, Exception("THOMANN USERS NOT FOUND"))
                }
            } else {
                onComplete(false, e)
            }
        }
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

    override fun kickUserFromThomann(
        thomannId: String,
        userId: String,
        userToKickId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        getThomann(thomannId) { firestoreThomann, e ->
            if (firestoreThomann != null) {
                if (firestoreThomann.userId == userId) {
                    val users = firestoreThomann.users
                    if (users != null) {
                        val user = users.firstOrNull { ftu -> ftu.userId == userToKickId }
                        if (user != null) {
                            firebaseFirestore
                                .collection(thomannsCollection)
                                .document(thomannId)
                                .update("users", FieldValue.arrayRemove(user))
                                .addOnSuccessListener { _ ->
                                    onComplete(true, null)
                                }
                                .addOnFailureListener { e ->
                                    onComplete(false, e)
                                }
                        } else {
                            onComplete(false, Exception("THOMANN USER WITH ID=$userId NOT FOUND"))
                        }
                    } else {
                        onComplete(false, Exception("THOMANN USERS NOT FOUND"))
                    }
                } else {
                    onComplete(false, Exception("THOMANN USER CAN NOT BE KICKED BECAUSE USERS IDS DO NOT MATCH"))
                }
            } else {
                onComplete(false, e)
            }
        }
    }
}