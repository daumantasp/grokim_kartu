package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp

class FirestoreUser(
    var id: String?,
    val visible: Boolean?,
    val registrationDate: Timestamp?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null)
}