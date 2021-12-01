package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.AuthenticationUser
import com.dauma.grokimkartu.data.users.entities.FirestoreUser

interface UsersDao {
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?, Exception?) -> Unit)
    fun loginUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit)

    // NOTE: Firestore User is used to expand native authenticated user with additional fields
    // Firestore User
    // TODO: probably should refactor. Why I need to pass userId? Its always the current user, that's
    // the purpose of this dao -- manipulate current user data
    fun setFirestoreUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun getFirestoreUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit)
    fun deleteFirestoreUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)

    // Authenticated User
    fun getAuthenticatedUserId(): String?
    fun logOut()
    fun isEmailVerified(): Boolean
    fun sendEmailVerification()
    fun deleteUser(onComplete: (Boolean, String?, Exception?) -> Unit)
    fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getUserDataProfiles(): List<AuthenticationUser>
    fun updateUserPassword(newPassword: String, onComplete: (Boolean, Exception?) -> Unit)
}