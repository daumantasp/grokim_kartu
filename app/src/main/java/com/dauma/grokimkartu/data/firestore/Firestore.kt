package com.dauma.grokimkartu.data.firestore

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

// Not possible to put business logic to Firestore Cloud Storage. Even though
// cloud functions could be used for this purpose, they are not included in free plan.
// So here added an additional layer as an imitation for business logic in DB
class Firestore(
    private val firebaseFirestore: FirebaseFirestore,
) {

    companion object {
        private const val usersCollection = "users"
        private const val playersCollection = "players"
    }

    fun getUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    val user = userDocumentSnapshot.toObject(FirestoreUser::class.java)
                    onComplete(user, null)
                } else {
                    onComplete(null, Exception("USER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    fun setUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit) {
        val myFirestore = Firestore(firebaseFirestore)
        myFirestore.

        firebaseFirestore
            .collection(usersCollection)
            .document(user.id)
            .update(mapOf(
                "id" to user.id,
                "visible" to user.visible
            ))
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    fun deleteUser(
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

     fun getProfile(userId: String, onComplete: (FirestoreProfile?, Exception?) -> Unit) {
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

    fun setProfile(userId: String, profile: FirestoreProfile, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .update("profile", profile)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(usersCollection)
            .document(userId)
            .update("profile", null)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(playersCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val players: MutableList<FirestorePlayer> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val id = queryDocumentSnapshot.getString("id") ?: ""
                    val visible = queryDocumentSnapshot.getBoolean("visible") ?: false
                    if (visible == true) {
                        var instrument: String = ""
                        var description: String? = null
                        val profileMap = queryDocumentSnapshot.get("profile") as MutableMap<*, *>?
                        if (profileMap != null) {
                            for (profile in profileMap) {
                                if (profile.key == "instrument") {
                                    instrument = profile.value as String
                                } else if (profile.key == "description") {
                                    description = profile.value as String?
                                }
                            }
                        }
                        val player = FirestorePlayer(
                            id,
                            Timestamp.now(),
                            visible,
                            "",
                            instrument,
                            description
                        )
                        players.add(player)
                    }
                }
                onComplete(true, players, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }
}
