package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CreateThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (input != null) {
            val valuesToSet: HashMap<String, Any> = hashMapOf()
            if (input?.userId != null) {
                valuesToSet["userId"] = input?.userId!!
            }
            if (input?.name != null) {
                valuesToSet["name"] = input?.name!!
            }
            if (input?.city != null) {
                valuesToSet["city"] = input?.city!!
            }
            if (input?.locked != null) {
                valuesToSet["locked"] = input?.locked!!
            }
            valuesToSet["creationDate"] = Timestamp.now()
            if (input?.validUntil != null) {
                valuesToSet["validUntil"] = input?.validUntil!!
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