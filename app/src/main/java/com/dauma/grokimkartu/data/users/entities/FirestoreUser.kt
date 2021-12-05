package com.dauma.grokimkartu.data.users.entities

class FirestoreUser(
    var id: String,
    var visible: Boolean?,
    var profile: FirestoreProfile?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", null, null)
}