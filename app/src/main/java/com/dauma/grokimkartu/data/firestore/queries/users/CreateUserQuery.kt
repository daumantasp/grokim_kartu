package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CreateUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val userToSet = getFirestoreUserToSet(input!!)
                firebaseFirestore
                    .collection(usersCollection)
                    .document(id!!)
                    // Because of the profile fields, you have to use merge
                    // READ MORE AT: https://stackoverflow.com/questions/46597327/difference-between-set-with-merge-true-and-update
                    .set(userToSet, SetOptions.merge())
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        this.onFailure(exception)
                    }
            } else {
                throw Exception("User is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun getFirestoreUserToSet(user: FirestoreUser) : HashMap<String, Any?> {
        val valuesToSet: HashMap<String, Any?> = hashMapOf()
        if (user.visible != null) {
            valuesToSet["visible"] = user.visible
        }
        valuesToSet["registrationDate"] = FieldValue.serverTimestamp()
        return valuesToSet
    }
}