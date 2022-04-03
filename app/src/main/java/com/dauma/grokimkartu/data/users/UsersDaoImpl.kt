package com.dauma.grokimkartu.data.users

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.firestore.storage.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.firestore.queries.users.CreateUserAndPlayerIfNeededQuery
import com.dauma.grokimkartu.data.firestore.queries.users.DeleteUserAndPlayerIfNeededQuery
import com.dauma.grokimkartu.data.firestore.queries.users.UpdateProfileAndPlayerIfNeededQuery
import com.dauma.grokimkartu.data.firestore.queries.users.UpdateUserAndPlayerIfNeededQuery
import com.dauma.grokimkartu.data.firestore.queries.users.DeleteProfileQuery
import com.dauma.grokimkartu.data.firestore.queries.users.ReadProfileQuery
import com.dauma.grokimkartu.data.firestore.queries.users.ReadUserQuery
import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao
import com.google.firebase.firestore.FirebaseFirestore

class UsersDaoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : UsersDao {
    override fun createUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreUser = toFirestoreUser(user)
        // TODO: fix !!
        CreateUserAndPlayerIfNeededQuery(firebaseFirestore)
            .withId(user.id ?: "")
            .withInput(firestoreUser!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun updateUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreUser = toFirestoreUser(user)
        UpdateUserAndPlayerIfNeededQuery(firebaseFirestore)
            .withId(user.id ?: "")
            .withInput(firestoreUser!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        DeleteUserAndPlayerIfNeededQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit) {
        ReadUserQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestoreUser ->
                val usersDao = toUserDao(firestoreUser)
                onComplete(usersDao, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun updateProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreProfile = toFirestoreProfile(profile)
        UpdateProfileAndPlayerIfNeededQuery(firebaseFirestore)
            .withId(userId)
            .withInput(firestoreProfile!!)
            .onSuccess { _ ->
                onComplete(true, null)
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
                firebaseStorage.deleteProfilePhoto(userId) { isSuccessful, e ->
                    onComplete(true, null)
                }
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit) {
        ReadProfileQuery(firebaseFirestore)
            .withId(userId)
            .onSuccess { firestoreProfile ->
                val profileDao = toProfileDao(firestoreProfile)
                onComplete(profileDao, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun getUserPhoto(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        firebaseStorage.downloadProfilePhoto(userId, onComplete)
    }

    override fun setUserPhoto(userId: String, photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseStorage.uploadProfilePhoto(userId, photo, onComplete)
    }

    override fun getUserIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        firebaseStorage.downloadProfilePhotoIcon(userId, onComplete)
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

    private fun toProfileDao(firestoreProfile: FirestoreProfile?) : ProfileDao? {
        var profileDao: ProfileDao? = null
        if (firestoreProfile != null) {
            profileDao = ProfileDao(firestoreProfile.name, firestoreProfile.instrument, firestoreProfile.description, firestoreProfile.city)
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