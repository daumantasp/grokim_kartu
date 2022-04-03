package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class ReadProfileQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<FirestoreProfile>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            firebaseFirestore
                .collection(usersCollection)
                .document(id!!)
                .get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    if (userDocumentSnapshot.exists()) {
                        var firestoreProfile = FirestoreProfile()
                        val profileEntries = userDocumentSnapshot.get("profile") as MutableMap<*, *>?
                        if (profileEntries != null) {
                            firestoreProfile = getFirestoreProfile(profileEntries)
                        }
                        this.onSuccess(firestoreProfile)
                    } else {
                        this.onFailure(Exception("User was not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    this.onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }

    fun getFirestoreProfile(entries: MutableMap<*, *>) : FirestoreProfile {
        val profile = FirestoreProfile()
        for (entry in entries) {
            if (entry.key == "name") {
                profile.name = entry.value as String?
            } else if (entry.key == "instrument") {
                profile.instrument = entry.value as String?
            } else if (entry.key == "description") {
                profile.description = entry.value as String?
            } else if (entry.key == "city") {
                profile.city = entry.value as String?
            }
        }
        return profile
    }
}