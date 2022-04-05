package com.dauma.grokimkartu.data.firestore.queries.thomanns

import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannActions
import com.dauma.grokimkartu.data.firestore.queries.FirestoreInputQuery
import com.google.firebase.firestore.FirebaseFirestore

class ReadThomannActionsQuery(firebaseFirestore: FirebaseFirestore)
    : FirestoreInputQuery<FirestoreThomannActions?, String>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            if (input != null) {
                readThomann() { firestoreThomann, exception ->
                    if (firestoreThomann != null) {
                        val actions = this.getActions(firestoreThomann)
                        this.onSuccess(actions)
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

    private fun getActions(firestoreThomann: FirestoreThomann) : FirestoreThomannActions {
        val userId = input!!
        val actions: FirestoreThomannActions
        if (firestoreThomann.userId == userId) {
            actions = FirestoreThomannActions(
                thomannId = id!!,
                isAccessible = true,
                isJoinable = false,
                isLeavable = false,
                isUpdatable = true
            )
        } else if (firestoreThomann.locked != true) {
            val users = firestoreThomann.users
            if (users != null) {
                val user = users.firstOrNull { ftu -> ftu.userId == userId }
                actions = FirestoreThomannActions(
                    thomannId = id!!,
                    isAccessible = true,
                    isJoinable = user == null,
                    isLeavable = user != null,
                    isUpdatable = false
                )
            } else {
                actions = FirestoreThomannActions(
                    thomannId = id!!,
                    isAccessible = true,
                    isJoinable = true,
                    isLeavable = false,
                    isUpdatable = false
                )
            }
        } else {
            val users = firestoreThomann.users
            if (users != null) {
                val user = users.firstOrNull { ftu -> ftu.userId == userId }
                if (user != null) {
                    actions = FirestoreThomannActions(
                        thomannId = id!!,
                        isAccessible = true,
                        isJoinable = false,
                        isLeavable = true,
                        isUpdatable = false
                    )
                } else {
                    actions = FirestoreThomannActions(
                        thomannId = id!!,
                        isAccessible = false,
                        isJoinable = false,
                        isLeavable = false,
                        isUpdatable = false
                    )
                }
            } else {
                actions = FirestoreThomannActions(
                    thomannId = id!!,
                    isAccessible = false,
                    isJoinable = false,
                    isLeavable = false,
                    isUpdatable = false
                )
            }
        }
        return actions
    }
}