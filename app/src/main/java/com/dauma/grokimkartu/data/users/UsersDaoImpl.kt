package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao

class UsersDaoImpl(private val firebase: Firestore) : UsersDao {
    override fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit) {
        firebase.getUser(userId) { firestoreUser, e ->
            val usersDao = toUserDao(firestoreUser)
            onComplete(usersDao, e)
        }
    }

    override fun setUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreUser = toFirestoreUser(user)
        firebase.setUser(firestoreUser!!, onComplete)
    }

    override fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebase.deleteUser(userId, onComplete)
    }

    override fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit) {
        firebase.getProfile(userId) { firestoreProfile, e ->
            val profileDao = toProfileDao(firestoreProfile)
            onComplete(profileDao, e)
        }
    }

    override fun setProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreProfile = toFirestoreProfile(profile)
        firebase.setProfile(userId, firestoreProfile!!, onComplete)
    }

    override fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebase.deleteProfile(userId, onComplete)
    }

    private fun toUserDao(firestoreUser: FirestoreUser?) : UserDao? {
        var userDao: UserDao? = null
        if (firestoreUser != null) {
            userDao = UserDao(firestoreUser.id, firestoreUser.name, firestoreUser.visible)
        }
        return userDao
    }

    private fun toFirestoreUser(userDao: UserDao?) : FirestoreUser? {
        var firestoreUser: FirestoreUser? = null
        if (userDao != null) {
            firestoreUser = FirestoreUser(userDao.id, userDao.name, userDao.visible)
        }
        return firestoreUser
    }

    private fun toProfileDao(firestoreProfile: FirestoreProfile?) : ProfileDao? {
        var profileDao: ProfileDao? = null
        if (firestoreProfile != null) {
            profileDao = ProfileDao(firestoreProfile.instrument, firestoreProfile.description)
        }
        return profileDao
    }

    private fun toFirestoreProfile(profileDao: ProfileDao?) : FirestoreProfile? {
        var firestoreProfile: FirestoreProfile? = null
        if (profileDao != null) {
            firestoreProfile = FirestoreProfile(profileDao.instrument, profileDao.description)
        }
        return firestoreProfile
    }
}