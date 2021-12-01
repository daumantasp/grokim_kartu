package com.dauma.grokimkartu.data.auth

import com.dauma.grokimkartu.data.auth.entities.AuthUser

interface AuthDao {
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?, Exception?) -> Unit)
    fun loginUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getAuthenticatedUserId(): String?
    fun logOut()
    fun isEmailVerified(): Boolean
    fun sendEmailVerification()
    fun deleteUser(onComplete: (Boolean, String?, Exception?) -> Unit)
    fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getUserDataProfiles(): List<AuthUser>
    fun updateUserPassword(newPassword: String, onComplete: (Boolean, Exception?) -> Unit)
}