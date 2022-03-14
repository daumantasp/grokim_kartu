package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreThomann(
    var id: String?,
    var userId: String?,
    var name: String?,
    var city: String?,
    var isLocked: Boolean?,
    @ServerTimestamp
    var creationDate: Timestamp?,
    @ServerTimestamp
    var validUntil: Timestamp?,
    var users: ArrayList<FirestoreThomannUser>?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null, null, null, null)
}