package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.ProfileDao
import com.dauma.grokimkartu.data.users.entities.UserDao

interface UsersDao {
    fun setUser(user: UserDao, onComplete: (Boolean, Exception?) -> Unit)
    fun getUser(userId: String, onComplete: (UserDao?, Exception?) -> Unit)
    fun deleteUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun setProfile(userId: String, profile: ProfileDao, onComplete: (Boolean, Exception?) -> Unit)
    fun getProfile(userId: String, onComplete: (ProfileDao?, Exception?) -> Unit)
    fun deleteProfile(userId: String, onComplete: (Boolean, Exception?) -> Unit)
}