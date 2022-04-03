package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomann>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val thommanToSet = getThomannToSet(input!!)
                firebaseFirestore
                    .collection(thomannsCollection)
                    .document(id!!)
                    // Because of the profile fields, you have to use merge
                    // READ MORE AT: https://stackoverflow.com/questions/46597327/difference-between-set-with-merge-true-and-update
                    .set(thommanToSet, SetOptions.merge())
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { exception ->
                        this.onFailure(exception)
                    }
            } else {
                throw Exception("Thomann is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }

    private fun getThomannToSet(thomann: FirestoreThomann) : HashMap<String, Any> {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        if (thomann.name != null) {
            valuesToSet["name"] = thomann.name
        }
        if (thomann.city != null) {
            valuesToSet["city"] = thomann.city
        }
        // TODO: restrict locking/unlocking from this method?
        if (thomann.locked != null) {
            valuesToSet["locked"] = thomann.locked
        }
        if (thomann.validUntil != null) {
            valuesToSet["validUntil"] = thomann.validUntil
        }
        return valuesToSet
    }
}