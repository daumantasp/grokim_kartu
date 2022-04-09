package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateProfileQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreProfile>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                val profileToSet = getProfileToSet(input!!)
                firebaseFirestore
                    .collection(usersCollection)
                    .document(id!!)
                    .set(profileToSet, SetOptions.merge())
                    .addOnSuccessListener { _ ->
                        this.onSuccess(null)
                    }
                    .addOnFailureListener { e ->
                        this.onFailure(e)
                    }
            } else {
                throw Exception("Profile is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun getProfileToSet(profile: FirestoreProfile) : HashMap<String, HashMap<String, Any>> {
        val valuesToSet: HashMap<String, Any> = hashMapOf()
        val profileToSet: HashMap<String, HashMap<String, Any>> = hashMapOf()
        profileToSet["profile"] = valuesToSet
        if (profile.name != null) {
            valuesToSet["name"] = profile.name!!
        }
        if (profile.instrument != null) {
            valuesToSet["instrument"] = profile.instrument!!
        }
        if (profile.description != null) {
            valuesToSet["description"] = profile.description!!
        }
        if (profile.city != null) {
            valuesToSet["city"] = profile.city!!
        }
        return profileToSet
    }
}