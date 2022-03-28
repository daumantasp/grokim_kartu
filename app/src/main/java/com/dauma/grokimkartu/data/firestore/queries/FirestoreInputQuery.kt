package com.dauma.grokimkartu.data.firestore.queries

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreInputQuery<T, K>(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<T>(firebaseFirestore) {
    protected var input: K? = null

    override fun withId(id: String): FirestoreInputQuery<T, K> {
        this.id = id
        return this
    }

    fun withInput(input: K): FirestoreInputQuery<T, K> {
        this.input = input
        return this
    }
}