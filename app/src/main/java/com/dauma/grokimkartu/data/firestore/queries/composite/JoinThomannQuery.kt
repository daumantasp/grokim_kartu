package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.UpdateThomannUserQuery
import com.google.firebase.firestore.FirebaseFirestore

class JoinThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, FirestoreThomannUser>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                doesUserCanJoinThomann() { canJoin, exception ->
                    if (exception != null) {
                        if (canJoin == true) {
                            this.updateThomannUsers() { isSuccessful, exception ->
                                if (isSuccessful) {
                                    onSuccess(null)
                                } else {
                                    onFailure(exception)
                                }
                            }
                        } else if (canJoin == false) {
                            onFailure(Exception("User is not allowed to join thomann"))
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

    private fun doesUserCanJoinThomann(onComplete: (Boolean?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreThomann ->
                if (firestoreThomann?.locked == true) {
                    onComplete(false, null)
                } else if (doesUserExist(id!!, firestoreThomann?.users ?: arrayListOf())) {
                    onComplete(false, null)
                } else {
                    onComplete(true, null)
                }
            }
            .onFailure { exception ->
                onFailure(exception)
            }
            .execute()
    }

    private fun doesUserExist(userId: String, users: ArrayList<FirestoreThomannUser>) : Boolean {
        for (user in users) {
            if (userId == user.userId) {
                return true
            }
        }
        return false
    }

    private fun updateThomannUsers(onComplete: (Boolean, Exception?) -> Unit) {
        UpdateThomannUserQuery(firebaseFirestore)
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
}