package com.dauma.grokimkartu.data.firestore.queries.users

import com.dauma.grokimkartu.data.firestore.entities.FirestoreUser
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.players.CreatePlayerForUserQuery
import com.google.firebase.firestore.FirebaseFirestore

class CreateUserAndPlayerIfNeededQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                createUser() { isSuccessful, exception ->
                    if (isSuccessful) {
                        if (needPlayerToBeCreated()) {
                            this.createPlayer() { isSuccessful, exception ->
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
                throw Exception("User is not provided")
            }
        } else {
            throw Exception("User id is not provided")
        }
    }

    private fun createUser(onComplete: (Boolean, Exception?) -> Unit) {
        CreateUserQuery(firebaseFirestore)
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

    private fun needPlayerToBeCreated() : Boolean {
        return input?.visible == true
    }

    private fun createPlayer(onComplete: (Boolean, Exception?) -> Unit) {
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