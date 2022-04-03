package com.dauma.grokimkartu.data.firestore.queries

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddThomannUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomannUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val valuesToSet: HashMap<String, Any> = hashMapOf()
                if (input?.userId != null) {
                    valuesToSet["userId"] = input?.userId!!
                }
                if (input?.userName != null) {
                    valuesToSet["userName"] = input?.userName!!
                }
                if (input?.thomannId != null) {
                    valuesToSet["thomannId"] = input?.thomannId!!
                }
                if (input?.amount != null) {
                    valuesToSet["amount"] = input?.amount!!
                }
                valuesToSet["joinDate"] = Timestamp.now()
                firebaseFirestore
                    .collection(thomannsCollection)
                    .document(id!!)
                    .update("users", FieldValue.arrayUnion(valuesToSet))
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