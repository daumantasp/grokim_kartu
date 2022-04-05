package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp

class FirestoreThomann(
    var id: String?,
    val userId: String?,
    val name: String?,
    val city: String?,
    val locked: Boolean?,
    val creationDate: Timestamp?,
    val validUntil: Timestamp?,
    val users: ArrayList<FirestoreThomannUser>?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null, null, null, null)
}