package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreUser(
    var id: String?,
    val visible: Boolean?,
    @ServerTimestamp
    val registrationDate: Timestamp?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null)
}