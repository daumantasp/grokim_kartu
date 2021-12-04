package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.FirestoreUser

interface UsersDao {
    fun setFirestoreUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun getFirestoreUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit)
    fun deleteFirestoreUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)
}