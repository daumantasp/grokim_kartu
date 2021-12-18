package com.dauma.grokimkartu.data.firestore

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser

// Not possible to put business logic to Firestore Cloud Storage. Even though
// cloud functions could be used for this purpose, they are not included in free plan.
// So here added an additional layer as an imitation for business logic in DB
interface Firestore {
    fun createUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun updateUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit)
    fun updateProfile(userId: String, profile: FirestoreProfile, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getProfile(userId: String, onComplete: (FirestoreProfile?, Exception?) -> Unit)
    fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit)
}
