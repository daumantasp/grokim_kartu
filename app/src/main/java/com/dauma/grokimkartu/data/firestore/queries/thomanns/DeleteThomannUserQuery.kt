package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DeleteThomannUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomannUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                firebaseFirestore
                    .collection(thomannsCollection)
                    .document(id!!)
                    .update("users", FieldValue.arrayRemove(input))
                    .addOnSuccessListener { _ ->
                        onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            } else {
                throw Exception("Input is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }
}