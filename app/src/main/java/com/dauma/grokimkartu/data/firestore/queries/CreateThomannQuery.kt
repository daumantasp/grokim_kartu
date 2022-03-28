package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CreateThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (inputObject != null) {
            val valuesToSet: HashMap<String, Any> = hashMapOf()
            if (inputObject?.userId != null) {
                valuesToSet["userId"] = inputObject?.userId!!
            }
            if (inputObject?.name != null) {
                valuesToSet["name"] = inputObject?.name!!
            }
            if (inputObject?.city != null) {
                valuesToSet["city"] = inputObject?.city!!
            }
            if (inputObject?.locked != null) {
                valuesToSet["locked"] = inputObject?.locked!!
            }
            valuesToSet["creationDate"] = Timestamp.now()
            if (inputObject?.validUntil != null) {
                valuesToSet["validUntil"] = inputObject?.validUntil!!
            }

            firebaseFirestore
                .collection(thomannsCollection)
                .add(valuesToSet)
                .addOnSuccessListener { _ ->
                    onSuccess(null)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            throw Exception("Input is not provided")
        }
    }
}