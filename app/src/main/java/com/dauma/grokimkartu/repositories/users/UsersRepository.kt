package com.dauma.grokimkartu.repositories.users

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.users.entities.Profile
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
    fun getUserProfile(onComplete: (Profile?, Exception?) -> Unit)
    fun setUserProfile(profile: Profile, onComplete: (Boolean, Exception?) -> Unit)
    fun getUserPhoto(onComplete: (Bitmap?, Exception?) -> Unit)
    fun setUserPhoto(photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit)
    fun getUserIcon(onComplete: (Bitmap?, Exception?) -> Unit)
}