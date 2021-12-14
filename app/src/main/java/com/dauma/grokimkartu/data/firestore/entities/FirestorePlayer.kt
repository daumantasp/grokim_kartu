package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestorePlayer(
    val userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null)
}