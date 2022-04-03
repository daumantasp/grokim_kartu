package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class DeleteProfileQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<Nothing>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            val emptySet: HashMap<String, Any> = hashMapOf()
            firebaseFirestore
                .collection(usersCollection)
                .document(id!!)
                .update("profile", emptySet)
                .addOnSuccessListener { _ ->
                    this.onSuccess(null)
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}