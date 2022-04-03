package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class FirestoreThomannUser(
    val userId: String?,
    val userName: String?,
    val thomannId: String?,
    val amount: Double?,
    // TODO: think of server timestamp proper use
    // FieldValue.serverTimestamp() can only be used with set() and update
    // https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/ServerTimestamp
//    @ServerTimestamp
    val joinDate: Timestamp?,
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null)
}