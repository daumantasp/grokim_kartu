package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreThomann(
    var id: String?,
    val userId: String?,
    val name: String?,
    val city: String?,
    val locked: Boolean?,
    @ServerTimestamp
    val creationDate: Timestamp?,
    @ServerTimestamp
    val validUntil: Timestamp?,
    val users: ArrayList<FirestoreThomannUser>?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null, null, null, null)
}