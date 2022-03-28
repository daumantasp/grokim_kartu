package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreProfile>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            val valuesToSet: HashMap<String, Any> = hashMapOf()
            if (input?.name != null) {
                valuesToSet["name"] = input?.name!!
            }
            if (input?.instrument != null) {
                valuesToSet["instrument"] = input?.instrument!!
            }
            if (input?.description != null) {
                valuesToSet["description"] = input?.description!!
            }
            if (input?.city != null) {
                valuesToSet["city"] = input?.city!!
            }

            firebaseFirestore
                .collection(usersCollection)
                .document(id!!)
                .update("profile", valuesToSet)
                .addOnSuccessListener { _ ->
                    onSuccess(null)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}