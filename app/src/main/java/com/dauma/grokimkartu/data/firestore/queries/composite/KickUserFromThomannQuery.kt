package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.RemoveThomannUserQuery
import com.google.firebase.firestore.FirebaseFirestore

data class KickUserFromThomanQueryInput(val userId: String, val userToKickId: String)

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
                                            onSuccess(null)
                                        } else {
                                            onFailure(exception)
                                        }
                                    }
                                } else {
                                    onFailure(Exception("User can not be kicked because he is not in the list"))
                                }
                            }
                        } else {
                            onFailure(Exception("User is not a creator of Thomann and thus has no rights to kick"))
                        }
                    } else {
                        onFailure(exception)
                    }
                }
            } else {
                throw Exception("Input is not provided")
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