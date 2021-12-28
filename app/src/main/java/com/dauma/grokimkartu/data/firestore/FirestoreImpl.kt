package com.dauma.grokimkartu.data.firestore

import android.util.Log
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
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
        if (profile.instrument != null) {
            valuesToSet["instrument"] = profile.instrument!!
        }
        if (profile.description != null) {
            valuesToSet["description"] = profile.description!!
        }

        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .update("profile", valuesToSet)
            .addOnSuccessListener { _ ->
                this.updatePlayerWhenProfileIsUpdatedTrigger(userId, profile, onComplete)
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
                            if (profile.key == "instrument") {
                                profileDao.instrument = profile.value as String
                            } else if (profile.key == "description") {
                                profileDao.description = profile.value as String?
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

    private fun setUser(user: FirestoreUser, isCreation: Boolean, onComplete: (Boolean, Exception?) -> Unit) {
        if (user.id == null) {
            Log.d(TAG, "Failed to setUser: missing user.id")
            return
        }
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (user.name != null) {
            valuesToSet["name"] = user.name!!
        }
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
                    this.addOrDeletePlayerWhenVisibilityChangesTrigger(user, onComplete)
                } else {
                    onComplete(true, null)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun addOrDeletePlayerWhenVisibilityChangesTrigger(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit) {
        val isUserVisible = user.visible!!
        if (isUserVisible) {
            getProfile(user.id!!) { firestoreProfile, e ->
                val firestorePlayer = FirestorePlayer(
                    user.id!!,
                    user.name,
                    firestoreProfile?.instrument ?: "",
                    firestoreProfile?.description ?: ""
                )
                this.setPlayer(firestorePlayer) { isSuccessful, e ->
                    onComplete(true, null)
                }
            }
        } else {
            deletePlayer(user.id!!) { isSuccessful, e ->
                onComplete(true, e)
            }
        }
    }

    private fun updatePlayerWhenProfileIsUpdatedTrigger(
        userId: String,
        profile: FirestoreProfile,
        onComplete: (Boolean, Exception?) -> Unit,
    ) {
        getUser(userId) { user, e ->
            if (user?.visible ?: false) {
                val firestorePlayer = FirestorePlayer(
                    user?.id,
                    user?.name,
                    profile.instrument,
                    profile.description
                )
                this.setPlayer(firestorePlayer, onComplete)
            } else {
                onComplete(true, null)
            }
        }
    }

    private fun deletePlayerWhenUserIsDeletedTrigger(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        // Try to delete, do not care if such player exist
        deletePlayer(userId) { isSuccessful, e ->
            onComplete(true, e)
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
}