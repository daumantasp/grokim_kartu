package com.dauma.grokimkartu.data.firestore.entities

import com.google.firebase.Timestamp

class FirestoreThomannUser(
    val userId: String?,
    val userName: String?,
    val thomannId: String?,
    val amount: Double?,
    val joinDate: Timestamp?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(null, null, null, null, null)
}