package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.queries.players.DeletePlayerDetailsQuery
import com.dauma.grokimkartu.data.firestore.queries.players.DeletePlayerQuery
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.google.firebase.firestore.FirebaseFirestore

class DeleteUserAndPlayerIfNeededQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<Nothing>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            deleteUser() { isSuccessful, exception ->
                if (isSuccessful) {
                    this.deletePlayer { _, _ ->
                        // Currently it is not checked if such player already exists
                        // And trying to delete a player which do not exist
                        // could lead to an exception, so calling here onFailure
                        // could be incorrect thing to do.
                        this.deletePlayerDetails { _, _ ->
                            this.onSuccess(null)
                        }
                    }
                } else {
                    this.onFailure(exception)
                }
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun deleteUser(onComplete: (Boolean, Exception?) -> Unit) {
        DeleteUserQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun deletePlayer(onComplete: (Boolean, Exception?) -> Unit) {
        DeletePlayerQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun deletePlayerDetails(onComplete: (Boolean, Exception?) -> Unit) {
        DeletePlayerDetailsQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }
}