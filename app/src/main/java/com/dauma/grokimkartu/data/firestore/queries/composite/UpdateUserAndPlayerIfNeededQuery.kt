package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.firestore.queries.DeletePlayerDetails
import com.dauma.grokimkartu.data.firestore.queries.DeletePlayerQuery
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.UpdateUserQuery
import com.google.firebase.firestore.FirebaseFirestore

class UpdateUserAndPlayerIfNeededQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                updateUser() { isSuccessful, exception ->
                    if (isSuccessful) {
                        if (input?.visible == true) {
                            this.updatePlayer() { isSuccessful, exception ->
                                if (isSuccessful) {
                                    onSuccess(null)
                                } else {
                                    onFailure(exception)
                                }
                            }
                        } else if (input?.visible == false) {
                            this.deletePlayer() { isSuccessful, exception ->
                                if (isSuccessful) {
                                    this.deletePlayerDetails() { isSuccessful, exception ->
                                        if (isSuccessful) {
                                            onSuccess(null)
                                        } else {
                                            onFailure(exception)
                                        }
                                    }
                                } else {
                                    // Currently it is not checked if such player already exists
                                    // And trying to delete a player which do not exist
                                    // could lead to an exception, so calling here onFailure
                                    // could be incorrect thing to do.
                                    onSuccess(null)
                                }
                            }
                        }
                    } else {
                        onFailure(exception)
                    }
                }
            } else {
                throw Exception("Input is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun updateUser(onComplete: (Boolean, Exception?) -> Unit) {
        UpdateUserQuery(firebaseFirestore)
            .withId(id!!)
            .withInput(input!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun updatePlayer(onComplete: (Boolean, Exception?) -> Unit) {
        CreatePlayerForUser(firebaseFirestore)
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
        DeletePlayerDetails(firebaseFirestore)
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