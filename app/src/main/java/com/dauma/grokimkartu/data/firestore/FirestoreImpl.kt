package com.dauma.grokimkartu.data.firestore

import android.util.Log
import com.dauma.grokimkartu.data.firestore.entities.*
import com.google.firebase.Timestamp
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
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                this.deletePlayerWhenUserIsDeletedTrigger(userId, onComplete)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    var user = userDocumentSnapshot.toObject(FirestoreUser::class.java)
                    user?.id = userId
                    onComplete(user, null)
                } else {
                    onComplete(null, Exception("USER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun updateProfile(userId: String, profile: FirestoreProfile, onComplete: (Boolean, Exception?) -> Unit) {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (profile.name != null) {
            valuesToSet["name"] = profile.name!!
        }
        if (profile.instrument != null) {
            valuesToSet["instrument"] = profile.instrument!!
        }
        if (profile.description != null) {
            valuesToSet["description"] = profile.description!!
        }
        if (profile.city != null) {
            valuesToSet["city"] = profile.city!!
        }

        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .update("profile", valuesToSet)
            .addOnSuccessListener { _ ->
                this.updatePlayerWhenProfileIsUpdatedTrigger(userId, onComplete)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        val emptySet: HashMap<String, Any> = hashMapOf()

        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .update("profile", emptySet)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getProfile(userId: String, onComplete: (FirestoreProfile?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    val profileDao = FirestoreProfile()
                    val profileMap = userDocumentSnapshot.get("profile") as MutableMap<*, *>?
                    if (profileMap != null) {
                        for (profile in profileMap) {
                            if (profile.key == "name") {
                                profileDao.name = profile.value as String?
                            } else if (profile.key == "instrument") {
                                profileDao.instrument = profile.value as String?
                            } else if (profile.key == "description") {
                                profileDao.description = profile.value as String?
                            } else if (profile.key == "city") {
                                profileDao.city = profile.value as String?
                            }
                        }
                    }
                    onComplete(profileDao, null)
                } else {
                    onComplete(null, Exception("USER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(playersCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val players: MutableList<FirestorePlayer> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    var player = queryDocumentSnapshot.toObject(FirestorePlayer::class.java)
                    player.userId = queryDocumentSnapshot.id
                    players.add(player)
                }
                onComplete(true, players, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }

    override fun getPlayerDetails(
        userId: String,
        onComplete: (FirestorePlayerDetails?, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(playerDetailsCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { playerDetailsDocumentSnapshot ->
                if (playerDetailsDocumentSnapshot.exists()) {
                    var playerDetails = playerDetailsDocumentSnapshot.toObject(FirestorePlayerDetails::class.java)
                    playerDetails?.userId = userId
                    onComplete(playerDetails, null)
                } else {
                    onComplete(null, Exception("PLAYER DETAILS WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun createThomann(
        thomann: FirestoreThomann,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        setThomann(thomann, true, onComplete)
    }

    override fun updateThomann(
        thomann: FirestoreThomann,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        setThomann(thomann, false, onComplete)
    }

    override fun deleteThomann(thomannId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(thomannsCollection)
            .document(thomannId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getThomanns(onComplete: (Boolean, List<FirestoreThomann>?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(thomannsCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val thomanns: MutableList<FirestoreThomann> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val thomann = queryDocumentSnapshot.toObject(FirestoreThomann::class.java)
                    thomanns.add(thomann)
                }
                onComplete(true, thomanns, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }

    private fun setUser(user: FirestoreUser, isCreation: Boolean, onComplete: (Boolean, Exception?) -> Unit) {
        if (user.id == null) {
            Log.d(TAG, "Failed to setUser: missing user.id")
            return
        }
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (user.visible != null) {
            valuesToSet["visible"] = user.visible!!
        }
        if (isCreation) {
            valuesToSet["registrationDate"] = Timestamp.now()
        }

        firebaseFirestore
            .collection(usersCollection)
            .document(user.id!!)
            // Because of the profile fields, you have to use merge
            // READ MORE AT: https://stackoverflow.com/questions/46597327/difference-between-set-with-merge-true-and-update
            .set(valuesToSet, SetOptions.merge())
            .addOnSuccessListener { _ ->
                if (user.visible != null) {
                    this.addOrDeletePlayerWhenVisibilityChangesTrigger(user.id!!, user.visible!!, onComplete)
                } else {
                    onComplete(true, null)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun setThomann(thomann: FirestoreThomann, isCreation: Boolean, onComplete: (Boolean, Exception?) -> Unit) {
        if (thomann.id == null) {
            Log.d(TAG, "Failed to setThomann: missing thomann.id")
            return
        }
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (thomann.name != null) {
            valuesToSet["name"] = thomann.name!!
        }
        if (thomann.city != null) {
            valuesToSet["city"] = thomann.city!!
        }
        if (thomann.isLocked != null) {
            valuesToSet["isLocked"] = thomann.isLocked!!
        }
        if (isCreation) {
            valuesToSet["creationDate"] = Timestamp.now()
        }
        if (thomann.validUntil != null) {
            valuesToSet["validUntil"] = thomann.validUntil!!
        }

        firebaseFirestore
            .collection(thomannsCollection)
            .document(thomann.id!!)
            // Because of the profile fields, you have to use merge
            // READ MORE AT: https://stackoverflow.com/questions/46597327/difference-between-set-with-merge-true-and-update
            .set(valuesToSet, SetOptions.merge())
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
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
                        this.setPlayer(firestorePlayer) { isSuccessful, e ->
                            this.setPlayerDetails(firestorePlayerDetails) { isSuccessful, e ->
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
                    this.setPlayer(firestorePlayer) { isSuccessful, e ->
                        this.setPlayerDetails(firestorePlayerDetails) { isSuccessful, e ->
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

    private fun setPlayer(player: FirestorePlayer, onComplete: (Boolean, Exception?) -> Unit) {
        if (player.userId == null) {
            Log.d(TAG, "Failed to setPlayer: missing player.userId")
            return
        }
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (player.name != null) {
            valuesToSet["name"] = player.name!!
        }
        if (player.instrument != null) {
            valuesToSet["instrument"] = player.instrument!!
        }
        if (player.description != null) {
            valuesToSet["description"] = player.description!!
        }
        if (player.city != null) {
            valuesToSet["city"] = player.city!!
        }

        firebaseFirestore
            .collection(playersCollection)
            .document(player.userId!!)
            .set(valuesToSet, SetOptions.merge())
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun deletePlayer(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(playersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun setPlayerDetails(playerDetails: FirestorePlayerDetails, onComplete: (Boolean, Exception?) -> Unit) {
        if (playerDetails.userId == null) {
            Log.d(TAG, "Failed to setPlayer: missing player.userId")
            return
        }
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (playerDetails.name != null) {
            valuesToSet["name"] = playerDetails.name!!
        }
        if (playerDetails.instrument != null) {
            valuesToSet["instrument"] = playerDetails.instrument!!
        }
        if (playerDetails.description != null) {
            valuesToSet["description"] = playerDetails.description!!
        }
        if (playerDetails.city != null) {
            valuesToSet["city"] = playerDetails.city!!
        }

        firebaseFirestore
            .collection(playerDetailsCollection)
            .document(playerDetails.userId!!)
            .set(valuesToSet, SetOptions.merge())
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun deletePlayerDetails(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(playerDetailsCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}