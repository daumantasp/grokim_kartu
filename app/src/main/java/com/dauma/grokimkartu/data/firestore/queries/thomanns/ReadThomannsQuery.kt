package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class ReadThomannsQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<List<FirestoreThomann>>(firebaseFirestore) {
    override fun execute() {
        firebaseFirestore
            .collection(thomannsCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val thomanns: MutableList<FirestoreThomann> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val thomann = queryDocumentSnapshot.toObject(FirestoreThomann::class.java)
                    thomann.id = queryDocumentSnapshot.id
                    thomanns.add(thomann)
                }
                onSuccess(thomanns)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}