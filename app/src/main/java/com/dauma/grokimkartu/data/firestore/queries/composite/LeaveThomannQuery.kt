package com.dauma.grokimkartu.data.firestore.queries.composite

import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.RemoveThomannUserQuery
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
                                    onSuccess(null)
                                } else {
                                    onFailure(exception)
                                }
                            }
                        } else {
                            onFailure(Exception("User can not leave Thomann because he is not in the list"))
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
                onFailure(exception)
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