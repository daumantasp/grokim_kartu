package com.dauma.grokimkartu.data.firestore.entities

class FirestoreProfile(
    var name: String?,
    var instrument: String?,
    var description: String?,
    var city: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null)
}