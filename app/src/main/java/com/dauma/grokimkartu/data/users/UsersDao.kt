package com.dauma.grokimkartu.data.users

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao

interface UsersDao {
    fun createUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit)
    fun updateUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit)
    fun updateProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit)
    fun getUserPhoto(userId: String, onComplete: (Bitmap?, Exception?) -> Unit)
    fun setUserPhoto(userId: String, photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit)
    fun getUserIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit)
}