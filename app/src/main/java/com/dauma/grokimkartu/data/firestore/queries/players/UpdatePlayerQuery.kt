package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdatePlayerQuery(firestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestorePlayer>(firestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
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
                    .collection(playersCollection)
                    .document(id!!)
                    .set(valuesToSet, SetOptions.merge())
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
            throw Exception("User id is not provided")
        }
    }
}