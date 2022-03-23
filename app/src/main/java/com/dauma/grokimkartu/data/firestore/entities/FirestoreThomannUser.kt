package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreThomannUser(
    var userId: String?,
    var userName: String?,
    var thomannId: String?,
    var amount: Double?,
    // TODO: think of server timestamp proper use
    // FieldValue.serverTimestamp() can only be used with set() and update
    // https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/ServerTimestamp
//    @ServerTimestamp
    var joinDate: Timestamp?,
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null)
}