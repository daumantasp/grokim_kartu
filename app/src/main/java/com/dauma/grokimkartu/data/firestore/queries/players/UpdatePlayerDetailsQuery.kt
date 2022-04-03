package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayerDetails
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdatePlayerDetailsQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestorePlayerDetails>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val detailsToSet = getDetailsToSet(input!!)
                firebaseFirestore
                    .collection(playerDetailsCollection)
                    .document(id!!)
                    .set(detailsToSet, SetOptions.merge())
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        this.onFailure(exception)
                    }
            } else {
                throw Exception("Player details is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun getDetailsToSet(details: FirestorePlayerDetails) : HashMap<String, Any> {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (details.name != null) {
            valuesToSet["name"] = details.name
        }
        if (details.instrument != null) {
            valuesToSet["instrument"] = details.instrument
        }
        if (details.description != null) {
            valuesToSet["description"] = details.description
        }
        if (details.city != null) {
            valuesToSet["city"] = details.city
        }
        return valuesToSet
    }
}