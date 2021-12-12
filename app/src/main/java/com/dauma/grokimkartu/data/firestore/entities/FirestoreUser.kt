package com.dauma.grokimkartu.data.firestore.entities

class FirestoreUser(
    var id: String,
    var visible: Boolean?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", null)
}