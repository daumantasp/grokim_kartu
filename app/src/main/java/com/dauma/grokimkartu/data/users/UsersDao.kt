package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entitites.AuthenticationUser
import com.dauma.grokimkartu.data.users.entitites.FirestoreUser

interface UsersDao {
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?, Exception?) -> Unit)
    fun addUserToFirestore(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUserFromFirestore(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun loginUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun isUserLoggedIn(): Boolean
    fun logOut()
    fun isEmailVerified(): Boolean
    fun sendEmailVerification()
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUser(onComplete: (Boolean, String?, Exception?) -> Unit)
    fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getAuthenticatedUserDataProfiles(): List<AuthenticationUser>
    fun updatePassword(newPassword: String, onComplete: (Boolean, Exception?) -> Unit)
}