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
                        val firestoreProfile = FirestoreProfile()
                        val profileMap = userDocumentSnapshot.get("profile") as MutableMap<*, *>?
                        if (profileMap != null) {
                            for (profile in profileMap) {
                                if (profile.key == "name") {
                                    firestoreProfile.name = profile.value as String?
                                } else if (profile.key == "instrument") {
                                    firestoreProfile.instrument = profile.value as String?
                                } else if (profile.key == "description") {
                                    firestoreProfile.description = profile.value as String?
                                } else if (profile.key == "city") {
                                    firestoreProfile.city = profile.value as String?
                                }
                            }
                        }
                        onSuccess(firestoreProfile)
                    } else {
                        onFailure(Exception("User was not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            throw Exception("User id is not provided")
        }
    }
}