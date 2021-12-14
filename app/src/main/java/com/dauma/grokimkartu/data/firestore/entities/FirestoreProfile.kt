package com.dauma.grokimkartu.data.firestore.entities

class FirestoreProfile(
    var instrument: String?,
    var description: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null)
}