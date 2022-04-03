package com.dauma.grokimkartu.data.firestore.queries.players

import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayer
import com.dauma.grokimkartu.data.firestore.entities.FirestorePlayerDetails
import com.dauma.grokimkartu.data.firestore.entities.FirestoreProfile
import com.dauma.grokimkartu.data.firestore.queries.FirestoreQuery
import com.dauma.grokimkartu.data.firestore.queries.users.ReadProfileQuery
import com.dauma.grokimkartu.data.firestore.queries.players.UpdatePlayerDetailsQuery
import com.dauma.grokimkartu.data.firestore.queries.players.UpdatePlayerQuery
import com.google.firebase.firestore.FirebaseFirestore

class CreatePlayerForUser(firebaseFirestore: FirebaseFirestore)
    : FirestoreQuery<Nothing>(firebaseFirestore) {
    override fun execute() {
        if (id != null) {
            readProfile() { firestoreProfile, exception ->
                if (firestoreProfile != null) {
                    val firestorePlayer = getPlayer(firestoreProfile)
                    this.updatePlayer(firestorePlayer) { isSuccessful, exception ->
                        if (isSuccessful) {
                            val firestorePlayerDetails = getPlayerDetails(firestoreProfile)
                            this.updatePlayerDetails(firestorePlayerDetails) { isSuccessful, exception ->
                                if (isSuccessful) {
                                    this.onSuccess(null)
                                } else {
                                    this.onFailure(exception)
                                }
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
            throw Exception("User id is not provided")
        }
    }

    private fun readProfile(onComplete: (FirestoreProfile?, Exception?) -> Unit) {
        ReadProfileQuery(firebaseFirestore)
            .withId(id!!)
            .onSuccess { firestoreProfile ->
                onComplete(firestoreProfile, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    private fun updatePlayer(
        player: FirestorePlayer,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        UpdatePlayerQuery(firebaseFirestore)
            .withId(id!!)
            .withInput(player)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun updatePlayerDetails(
        playerDetails: FirestorePlayerDetails,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        UpdatePlayerDetailsQuery(firebaseFirestore)
            .withId(id!!)
            .withInput(playerDetails)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun getPlayer(profile: FirestoreProfile?) : FirestorePlayer {
        return FirestorePlayer(
            userId = id,
            name = profile?.name ?: "",
            instrument = profile?.instrument ?: "",
            description = profile?.description ?: "",
            city = profile?.city ?: ""
        )
    }

    private fun getPlayerDetails(profile: FirestoreProfile?) : FirestorePlayerDetails {
        return FirestorePlayerDetails(
            userId = id,
            name = profile?.name ?: "",
            instrument = profile?.instrument ?: "",
            description = profile?.description ?: "",
            city = profile?.city ?: ""
        )
    }
}