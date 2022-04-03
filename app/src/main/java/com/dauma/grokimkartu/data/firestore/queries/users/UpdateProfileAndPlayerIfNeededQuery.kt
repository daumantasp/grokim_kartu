package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.players.CreatePlayerForUserQuery
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileAndPlayerIfNeededQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreProfile>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                updateProfile() { isSuccessful, exception ->
                    if (isSuccessful) {
                        this.isPlayerUpdateNeeded { isNeeded, exception ->
                            if (exception == null) {
                                if (isNeeded == true) {
                                    this.updatePlayer() { isSuccessful, exception ->
                                        if (isSuccessful) {
                                            this.onSuccess(null)
                                        } else {
                                            this.onFailure(exception)
                                        }
                                    }
                                } else {
                                    this.onSuccess(null)
                                }
                            } else {
                                this.onFailure(exception)
                            }
                        }
                    } else {
                        this.onFailure(exception)
                    }
                }
            } else {
                throw Exception("Profile is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun updateProfile(onComplete: (Boolean, Exception?) -> Unit) {
        UpdateProfileQuery(firebaseFirestore)
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

    private fun isPlayerUpdateNeeded(onComplete: (Boolean?, Exception?) -> Unit) {
        ReadUserQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreUser ->
                onComplete(firestoreUser?.visible == true, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    private fun updatePlayer(onComplete: (Boolean, Exception?) -> Unit) {
        CreatePlayerForUserQuery(firebaseFirestore)
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