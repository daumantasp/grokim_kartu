package com.dauma.grokimkartu.data.firestore.entities

class FirestoreUser(
    var id: String?,
    var name: String?,
    var visible: Boolean?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null)
}