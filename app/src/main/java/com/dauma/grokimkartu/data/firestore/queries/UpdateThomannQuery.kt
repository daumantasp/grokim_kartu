package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val valuesToSet: HashMap<String, Any> = hashMapOf()
                if (input?.name != null) {
                    valuesToSet["name"] = input?.name!!
                }
                if (input?.city != null) {
                    valuesToSet["city"] = input?.city!!
                }
                // TODO: restrict locking/unlocking from this method?
                if (input?.locked != null) {
                    valuesToSet["locked"] = input?.locked!!
                }
                if (input?.validUntil != null) {
                    valuesToSet["validUntil"] = input?.validUntil!!
                }

                firebaseFirestore
                    .collection(thomannsCollection)
                    .document(id!!)
                    // Because of the profile fields, you have to use merge
                    // READ MORE AT: https://stackoverflow.com/questions/46597327/difference-between-set-with-merge-true-and-update
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
            throw Exception("Thomann id is not provided")
        }
    }
}