package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.auth.entities.AuthUser
import com.dauma.grokimkartu.data.users.entities.FirestoreUser

interface UsersRepository {
    fun isUserLoggedIn(): Boolean
    fun registerUser(email: String, password: String, name: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun sendEmailVerification()
    fun isEmailVerified(): Boolean
    fun loginUser(email: String, password: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun logOut()
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun deleteUser(onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun getAuthenticatedUserData(): AuthUser
    fun updatePassword(newPassword: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun getUserData(onComplete: (FirestoreUser?, Exception?) -> Unit)
    fun setUserData(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    // TODO: think about  getAuthenticatedUserData and getUserData merging into one method
    // hide complex logic in the repository
}