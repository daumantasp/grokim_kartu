package com.dauma.grokimkartu.data.firestore.entities

class FirestorePlayerDetails(
    var userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null)
}