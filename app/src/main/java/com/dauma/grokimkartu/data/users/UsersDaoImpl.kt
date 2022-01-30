package com.dauma.grokimkartu.data.users

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.firestore.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao

class UsersDaoImpl(
    private val firebase: Firestore,
    private val firebaseStorage: FirebaseStorage,
) : UsersDao {
    override fun createUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreUser = toFirestoreUser(user)
        firebase.createUser(firestoreUser!!, onComplete)
    }

    override fun updateUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreUser = toFirestoreUser(user)
        firebase.updateUser(firestoreUser!!, onComplete)
    }

    override fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebase.deleteUser(userId, onComplete)
    }

    override fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit) {
        firebase.getUser(userId) { firestoreUser, e ->
            val usersDao = toUserDao(firestoreUser)
            onComplete(usersDao, e)
        }
    }

    override fun updateProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreProfile = toFirestoreProfile(profile)
        firebase.updateProfile(userId, firestoreProfile!!) { isSuccessful, e ->
            if (isSuccessful) {
                if (profile.photo != null) {
                    firebaseStorage.uploadProfilePhoto(userId, profile.photo!!) { isSuccessful, e ->
                        onComplete(isSuccessful, null)
                    }
                } else {
                    onComplete(true, null)
                }
            } else {
                onComplete(false, e)
            }
        }
    }

    override fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebase.deleteProfile(userId) { isSuccessful, e ->
            if (isSuccessful) {
                firebaseStorage.deleteProfilePhoto(userId) { isSuccessful, e ->
                    onComplete(true, null)
                }
            } else {
                onComplete(false, e)
            }
        }
    }

    override fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit) {
        firebase.getProfile(userId) { firestoreProfile, e ->
            firebaseStorage.downloadProfilePhoto(userId) { profilePhoto, e ->
                val profileDao = toProfileDao(firestoreProfile, profilePhoto)
                onComplete(profileDao, e)
            }
        }
    }

    private fun toUserDao(firestoreUser: FirestoreUser?) : UserDao? {
        var userDao: UserDao? = null
        if (firestoreUser != null) {
            userDao = UserDao(firestoreUser.id, firestoreUser.visible, firestoreUser.registrationDate)
        }
        return userDao
    }

    private fun toFirestoreUser(userDao: UserDao?) : FirestoreUser? {
        var firestoreUser: FirestoreUser? = null
        if (userDao != null) {
            firestoreUser = FirestoreUser(userDao.id, userDao.visible, userDao.registrationDate)
        }
        return firestoreUser
    }

    private fun toProfileDao(firestoreProfile: FirestoreProfile?, photo: Bitmap?) : ProfileDao? {
        var profileDao: ProfileDao? = null
        if (firestoreProfile != null) {
            profileDao = ProfileDao(firestoreProfile.name, firestoreProfile.instrument, firestoreProfile.description, photo, firestoreProfile.city)
        }
        return profileDao
    }

    private fun toFirestoreProfile(profileDao: ProfileDao?) : FirestoreProfile? {
        var firestoreProfile: FirestoreProfile? = null
        if (profileDao != null) {
            firestoreProfile = FirestoreProfile(profileDao.name, profileDao.instrument, profileDao.description, profileDao.city)
        }
        return firestoreProfile
    }
}