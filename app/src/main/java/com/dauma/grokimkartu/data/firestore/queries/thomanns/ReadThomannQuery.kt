package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class ReadThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(thomannsCollection)
                .document(id!!)
                .get()
                .addOnSuccessListener { thomannDocumentSnapshot ->
                    if (thomannDocumentSnapshot.exists()) {
                        val thomann = thomannDocumentSnapshot.toObject(FirestoreThomann::class.java)
                        thomann?.id = id
                        this.onSuccess(thomann)
                    } else {
                        this.onFailure(Exception("Thomann was not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }
}