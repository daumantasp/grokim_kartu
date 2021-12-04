package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.repositories.users.entities.User

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
    fun updatePassword(newPassword: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun getUserData(onComplete: (User?, Exception?) -> Unit)
    fun setUserData(user: User, onComplete: (Boolean, Exception?) -> Unit)
}