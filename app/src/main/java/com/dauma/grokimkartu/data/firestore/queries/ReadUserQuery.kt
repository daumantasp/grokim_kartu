package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.google.firebase.firestore.FirebaseFirestore

class ReadUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<FirestoreUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(usersCollection)
                .document(id!!)
                .get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    if (userDocumentSnapshot.exists()) {
                        val user = userDocumentSnapshot.toObject(FirestoreUser::class.java)
                        user?.id = id
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User was not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}