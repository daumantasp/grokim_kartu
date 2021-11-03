package com.dauma.grokimkartu.data.users.entitites

class FirestoreUser(
    var id: String,
    val name: String
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", "")
}