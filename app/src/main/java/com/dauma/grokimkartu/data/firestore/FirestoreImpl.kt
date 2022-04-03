package com.dauma.grokimkartu.data.firestore

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : Firestore {
    companion object {
        private const val thomannsCollection = "thomanns"
    }
}