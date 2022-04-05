package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddThomannUserQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomannUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val thomannUserToSet = getThomannUserToSet(input!!)
                firebaseFirestore
                    .collection(thomannsCollection)
                    .document(id!!)
                    .update("users", FieldValue.arrayUnion(thomannUserToSet))
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        this.onFailure(exception)
                    }
            } else {
                throw Exception("Thomann User is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }

    private fun getThomannUserToSet(thomannUser: FirestoreThomannUser) : HashMap<String, Any?> {
        val valuesToSet: HashMap<String, Any?> = hashMapOf()
        if (thomannUser.userId != null) {
            valuesToSet["userId"] = thomannUser.userId
        }
        if (thomannUser.userName != null) {
            valuesToSet["userName"] = thomannUser.userName
        }
        if (thomannUser.thomannId != null) {
            valuesToSet["thomannId"] = thomannUser.thomannId
        }
        if (thomannUser.amount != null) {
            valuesToSet["amount"] = thomannUser.amount
        }
        // FieldValue.serverTimestamp() can only be used with set() and update
//        valuesToSet["joinDate"] = FieldValue.serverTimestamp()
        valuesToSet["joinDate"] = Timestamp.now()
        return valuesToSet
    }
}