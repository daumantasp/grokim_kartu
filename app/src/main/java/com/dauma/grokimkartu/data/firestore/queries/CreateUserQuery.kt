package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CreateUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val valuesToSet: HashMap<String, Any> = hashMapOf()
                if (input?.visible != null) {
                    valuesToSet["visible"] = input?.visible!!
                }
                valuesToSet["registrationDate"] = Timestamp.now()

                firebaseFirestore
                    .collection(usersCollection)
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
            throw Exception("User id is not provided")
        }
    }
}