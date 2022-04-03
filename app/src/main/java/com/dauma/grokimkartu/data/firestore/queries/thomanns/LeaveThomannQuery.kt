package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore

class LeaveThomannQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<Nothing, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                doesUserCanLeaveThomann() { canLeave, exception ->
                    if (exception != null) {
                        if (canLeave == true) {
                            this.removeThomannUser() { isSuccessful, exception ->
                                if (isSuccessful) {
                                    this.onSuccess(null)
                                } else {
                                    this.onFailure(exception)
                                }
                            }
                        } else {
                            this.onFailure(Exception("User can not leave Thomann because he is not in the list"))
                        }
                    } else {
                        this.onFailure(exception)
                    }
                }
            } else {
                throw Exception("User id is not provided")
            }
        } else {
            throw Exception("Thomann id is not provided")
        }
    }

    private fun doesUserCanLeaveThomann(onComplete: (Boolean?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreThomann ->
                val userWhoWantsToLeaveId = input!!
                val user = firestoreThomann?.users?.firstOrNull { ftu -> ftu.userId == userWhoWantsToLeaveId }
                if (user != null) {
                    onComplete(true, null)
                } else {
                    onComplete(false, null)
                }
            }
            .onFailure { exception ->
                this.onFailure(exception)
            }
            .execute()
    }

    private fun removeThomannUser(onComplete: (Boolean, Exception?) -> Unit) {
        RemoveThomannUserQuery(firebaseFirestore)
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