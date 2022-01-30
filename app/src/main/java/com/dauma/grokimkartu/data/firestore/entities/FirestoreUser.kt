package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreUser(
    var id: String?,
    var visible: Boolean?,
    @ServerTimestamp
    var registrationDate: Timestamp?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null)
}