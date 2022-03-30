package com.dauma.grokimkartu.data.firestore

import android.util.Log
import com.dauma.grokimkartu.data.firestore.entities.*
import com.dauma.grokimkartu.data.firestore.queries.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : Firestore {
    companion object {
        private const val TAG = "FirestoreImpl"
        private const val usersCollection = "users"
        private const val playersCollection = "players"
        private const val playerDetailsCollection = "playerDetails"
        private const val thomannsCollection = "thomanns"
    }

    override fun createUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit) {
        this.setUser(user, true, onComplete)
    }

    override fun updateUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit) {
        this.setUser(user, false, onComplete)
    }

    override fun deleteUser(
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        DeleteUserQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { _ ->
                this.deletePlayerWhenUserIsDeletedTrigger(userId, onComplete)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun getUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit) {
        ReadUserQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestoreUser ->
                onComplete(firestoreUser, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun updateProfile(userId: String, profile: FirestoreProfile, onComplete: (Boolean, Exception?) -> Unit) {
        UpdateProfileQuery(firebaseFirestore)
            .withId(userId)
            .withInput(profile)
            .onSuccess { _ ->
                this.updatePlayerWhenProfileIsUpdatedTrigger(userId, onComplete)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        DeleteProfileQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
    }

    override fun getProfile(userId: String, onComplete: (FirestoreProfile?, Exception?) -> Unit) {
        ReadProfileQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestoreProfile ->
                onComplete(firestoreProfile, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit) {
        ReadPlayersQuery(firebaseFirestore)
            .onSuccess { firestorePlayers ->
                onComplete(true, firestorePlayers, null)
            }
            .onFailure { exception ->
                onComplete(false, null, exception)
            }
            .execute()
    }

    override fun getPlayerDetails(
        userId: String,
        onComplete: (FirestorePlayerDetails?, Exception?) -> Unit
    ) {
        ReadPlayerDetailsQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestorePlayerDetails ->
                onComplete(firestorePlayerDetails, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun createThomann(
        thomann: FirestoreThomann,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        CreateThomannQuery(firebaseFirestore)
            .withInput(thomann)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun updateThomann(
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

    override fun getThomanns(onComplete: (Boolean, List<FirestoreThomann>?, Exception?) -> Unit) {
        ReadThomannsQuery(firebaseFirestore)
            .onSuccess { firestoreThomanns ->
                onComplete(true, firestoreThomanns, null)
            }
            .onFailure { exception ->
                onComplete(false, null, exception)
            }
            .execute()
    }

    override fun getThomann(thomannId: String, onComplete: (FirestoreThomann?, Exception?) -> Unit) {
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

    override fun joinThomann(
        thomannId: String,
        user: FirestoreThomannUser,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        UpdateThomannUserQuery(firebaseFirestore)
            .withId(thomannId)
            .withInput(user)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
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

    private fun setUser(user: FirestoreUser, isCreation: Boolean, onComplete: (Boolean, Exception?) -> Unit) {
        if (isCreation) {
            CreateUserQuery(firebaseFirestore)
                .withId(user.id ?: "")
                .withInput(user)
                .onSuccess { _ ->
                    if (user.visible != null) {
                        this.addOrDeletePlayerWhenVisibilityChangesTrigger(user.id!!, user.visible!!, onComplete)
                    } else {
                        onComplete(true, null)
                    }
                }
                .onFailure { exception ->
                    onComplete(false, exception)
                }
                .execute()
        } else {
            UpdateUserQuery(firebaseFirestore)
                .withId(user.id ?: "")
                .withInput(user)
                .onSuccess { _ ->
                    if (user.visible != null) {
                        this.addOrDeletePlayerWhenVisibilityChangesTrigger(user.id!!, user.visible!!, onComplete)
                    } else {
                        onComplete(true, null)
                    }
                }
                .onFailure { exception ->
                    onComplete(false, exception)
                }
                .execute()
        }
    }

    private fun addOrDeletePlayerWhenVisibilityChangesTrigger(
        userId: String,
        isVisible: Boolean,
        onComplete: (Boolean, Exception?) -> Unit,
    ) {
        if (isVisible) {
            getUser(userId) { firestoreUser, e ->
                if (firestoreUser != null) {
                    this.getProfile(userId) { firestoreProfile, e ->
                        val firestorePlayer = FirestorePlayer(
                            firestoreUser.id,
                            firestoreProfile?.name ?: "",
                            firestoreProfile?.instrument ?: "",
                            firestoreProfile?.description ?: "",
                            firestoreProfile?.city ?: ""
                        )
                        val firestorePlayerDetails = FirestorePlayerDetails(
                            firestoreUser.id,
                            firestoreProfile?.name ?: "",
                            firestoreProfile?.instrument ?: "",
                            firestoreProfile?.description ?: "",
                            firestoreProfile?.city ?: ""
                        )
                        this.setPlayer(userId, firestorePlayer) { isSuccessful, e ->
                            this.setPlayerDetails(userId, firestorePlayerDetails) { isSuccessful, e ->
                                onComplete(true, e)
                            }
                        }
                    }
                } else {
                    onComplete(false, e)
                }
            }
        } else {
            deletePlayer(userId) { isSuccessful, e ->
                this.deletePlayerDetails(userId, onComplete)
            }
        }
    }

    private fun updatePlayerWhenProfileIsUpdatedTrigger(
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit,
    ) {
        getUser(userId) { firestoreUser, e ->
            if (firestoreUser?.visible ?: false) {
                this.getProfile(userId) { firestoreProfile, e ->
                    val firestorePlayer = FirestorePlayer(
                        firestoreUser?.id,
                        firestoreProfile?.name ?: "",
                        firestoreProfile?.instrument ?: "",
                        firestoreProfile?.description ?: "",
                        firestoreProfile?.city ?: ""
                    )
                    val firestorePlayerDetails = FirestorePlayerDetails(
                        firestoreUser?.id,
                        firestoreProfile?.name ?: "",
                        firestoreProfile?.instrument ?: "",
                        firestoreProfile?.description ?: "",
                        firestoreProfile?.city ?: ""
                    )
                    this.setPlayer(userId, firestorePlayer) { isSuccessful, e ->
                        this.setPlayerDetails(userId, firestorePlayerDetails) { isSuccessful, e ->
                            onComplete(true, e)
                        }
                    }
                }
            } else {
                onComplete(true, null)
            }
        }
    }

    private fun deletePlayerWhenUserIsDeletedTrigger(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        // Try to delete, do not care if such player exist
        deletePlayer(userId) { isSuccessful, e ->
            this.deletePlayerDetails(userId) { isSuccessful, e ->
                onComplete(true, e)
            }
        }
    }

    private fun setPlayer(userId: String, player: FirestorePlayer, onComplete: (Boolean, Exception?) -> Unit) {
        UpdatePlayerQuery(firebaseFirestore)
            .withId(userId)
            .withInput(player)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun deletePlayer(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        DeletePlayerQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun setPlayerDetails(userId: String, playerDetails: FirestorePlayerDetails, onComplete: (Boolean, Exception?) -> Unit) {
        UpdatePlayerDetailsQuery(firebaseFirestore)
            .withId(userId)
            .withInput(playerDetails)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun deletePlayerDetails(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        DeletePlayerDetails(firebaseFirestore)
            .withId(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }
}