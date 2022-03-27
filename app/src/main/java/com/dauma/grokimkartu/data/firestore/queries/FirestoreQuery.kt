package com.dauma.grokimkartu.data.firestore.queries

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreQuery<T>(
    protected val firebaseFirestore: FirebaseFirestore
) {
    companion object {
        const val TAG = "FirestoreImpl"
        const val usersCollection = "users"
        const val playersCollection = "players"
        const val playerDetailsCollection = "playerDetails"
        const val thomannsCollection = "thomanns"
    }

    protected var id: String? = null
    protected var onSuccess: ((T?) -> Unit) = {}
    protected var onFailure: (Exception?) -> Unit = {}

    abstract fun execute()

    fun withId(id: String) : FirestoreQuery<T> {
        this.id = id
        return this
    }

    fun onSuccess(onSuccessListener: (T?) -> Unit) : FirestoreQuery<T> {
        this.onSuccess = onSuccessListener
        return this
    }

    fun onFailure(onFailureListener: (Exception?) -> Unit) : FirestoreQuery<T> {
        this.onFailure = onFailureListener
        return this
    }
}