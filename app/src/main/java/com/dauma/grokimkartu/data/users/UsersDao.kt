package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.FirestoreUser

interface UsersDao {
    // NOTE: Firestore User is used to expand native authenticated user with additional fields
    // Firestore User
    // TODO: probably should refactor. Why I need to pass userId? Its always the current user, that's
    // the purpose of this dao -- manipulate current user data
    fun setFirestoreUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit)
    fun getFirestoreUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit)
    fun deleteFirestoreUser(userId: String, onComplete: (Boolean, Exception?) -> Unit)
}