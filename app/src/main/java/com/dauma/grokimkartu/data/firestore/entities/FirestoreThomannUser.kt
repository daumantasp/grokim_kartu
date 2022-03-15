package com.dauma.grokimkartu.data.firestore.entities

class FirestoreThomannUser(
    var userId: String?,
    var userName: String?,
    var amount: Double?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null)
}