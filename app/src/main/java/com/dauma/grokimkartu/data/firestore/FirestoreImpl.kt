package com.dauma.grokimkartu.data.firestore

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannActions
import com.dauma.grokimkartu.data.firestore.queries.*
import com.dauma.grokimkartu.data.firestore.queries.composite.DeleteThomannQuery
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : Firestore {
    companion object {
        private const val thomannsCollection = "thomanns"
    }
}