package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.thomanns.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.thomanns.RemoveThomannUserQuery
import com.dauma.grokimkartu.data.firestore.queries.thomanns.inputs.KickUserFromThomanQueryInput
import com.google.firebase.firestore.FirebaseFirestore

class KickUserFromThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, KickUserFromThomanQueryInput>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                readThomann() { firestoreThomann, exception ->
                    if (firestoreThomann != null) {
                        if (firestoreThomann.userId == input!!.userId) {
                            val users = firestoreThomann.users
                            if (users != null) {
                                val user = users.firstOrNull { ftu -> ftu.userId == input!!.userToKickId }
                                if (user != null) {
                                    this.kickUser { isSuccessful, exception ->
                                        if (isSuccessful) {
                                            this.onSuccess(null)
                                        } else {
                                            this.onFailure(exception)
                                        }
                                    }
                                } else {
                                    this.onFailure(Exception("User can not be kicked because he is not in the list"))
                                }
                            }
                        } else {
                            this.onFailure(Exception("User is not a creator of Thomann and thus has no rights to kick"))
                        }
                    } else {
                        this.onFailure(exception)
                    }
                }
            } else {
                throw Exception("KickUserFromThomanQueryInput is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }

    private fun readThomann(onComplete: (FirestoreThomann?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreThomann ->
                onComplete(firestoreThomann, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    private fun kickUser(onComplete: (Boolean, Exception?) -> Unit) {
        RemoveThomannUserQuery(firebaseFirestore)
            .withId(id!!)
            .withInput(input!!.userToKickId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }
}