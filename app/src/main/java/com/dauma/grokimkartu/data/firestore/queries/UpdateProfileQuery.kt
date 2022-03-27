package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreProfile>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            val valuesToSet: HashMap<String, Any> = hashMapOf()
            if (inputObject?.name != null) {
                valuesToSet["name"] = inputObject?.name!!
            }
            if (inputObject?.instrument != null) {
                valuesToSet["instrument"] = inputObject?.instrument!!
            }
            if (inputObject?.description != null) {
                valuesToSet["description"] = inputObject?.description!!
            }
            if (inputObject?.city != null) {
                valuesToSet["city"] = inputObject?.city!!
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