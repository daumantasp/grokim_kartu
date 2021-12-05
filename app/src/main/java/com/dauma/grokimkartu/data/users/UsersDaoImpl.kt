package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao
import com.google.firebase.firestore.FirebaseFirestore

class UsersDaoImpl(private val firebaseFirestore: FirebaseFirestore) : UsersDao {
    companion object {
        private const val usersCollection = "users"
    }

    override fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    val user = userDocumentSnapshot.toObject(UserDao::class.java)
                    onComplete(user, null)
                } else {
                    onComplete(null, Exception("USER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun setUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(user.id)
            .set(user)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deleteUser(
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    val profileDao = ProfileDao()
                    val profileMap = userDocumentSnapshot.get("profile") as MutableMap<*, *>
                    for (profile in profileMap) {
                        if (profile.key == "instrument") {
                            profileDao.instrument = profile.value as String
                        } else if (profile.key == "description") {
                            profileDao.description = profile.value as String?
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

    override fun setProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .update("profile", profile)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .update("profile", null)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}