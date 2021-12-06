package com.dauma.grokimkartu.data.players.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

class PlayerDao(
    val userId: String,
    @ServerTimestamp
    val registrationDate: Timestamp,
    val visible: Boolean,
    val name: String,
    val instrument: String,
    val description: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", Timestamp.now(), false, "", "", null)
}