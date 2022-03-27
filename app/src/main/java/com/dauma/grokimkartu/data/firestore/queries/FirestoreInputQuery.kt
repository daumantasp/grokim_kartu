package com.dauma.grokimkartu.data.firestore.queries

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreInputQuery<T, K>(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<T>(firebaseFirestore) {
    protected var inputObject: K? = null

    fun withInputObject(inputObject: K): FirestoreInputQuery<T, K> {
        this.inputObject = inputObject
        return this
    }
}