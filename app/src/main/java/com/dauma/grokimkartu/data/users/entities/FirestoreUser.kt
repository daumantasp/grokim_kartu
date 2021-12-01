package com.dauma.grokimkartu.data.users.entities

class FirestoreUser(
    var id: String,
    var name: String?,
    var showMe: Boolean?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", null, null)
}