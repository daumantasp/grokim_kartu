package com.dauma.grokimkartu.data.firestore.queries

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreInputQuery<T, K>(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<T>(firebaseFirestore) {
    protected var inputObject: K? = null

    override fun withId(id: String): FirestoreInputQuery<T, K> {
        this.id = id
        return this
    }

    fun withInputObject(inputObject: K): FirestoreInputQuery<T, K> {
        this.inputObject = inputObject
        return this
    }
}