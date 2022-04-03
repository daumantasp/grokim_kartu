package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CreateThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (input != null) {
            val thomannToSet = getThomannToSet(input!!)
            firebaseFirestore
                .collection(thomannsCollection)
                .add(thomannToSet)
                .addOnSuccessListener { _ ->
                    this.onSuccess(null)
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("Thomann is not provided")
        }
    }

    private fun getThomannToSet(thomann: FirestoreThomann) : HashMap<String, Any> {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (thomann.userId != null) {
            valuesToSet["userId"] = thomann.userId!!
        }
        if (thomann.name != null) {
            valuesToSet["name"] = thomann.name!!
        }
        if (thomann.city != null) {
            valuesToSet["city"] = thomann.city!!
        }
        if (thomann.locked != null) {
            valuesToSet["locked"] = thomann.locked!!
        }
        valuesToSet["creationDate"] = Timestamp.now()
        if (thomann.validUntil != null) {
            valuesToSet["validUntil"] = thomann.validUntil!!
        }
        return valuesToSet
    }
}