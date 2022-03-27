package com.dauma.grokimkartu.data.firestore.entities

class FirestoreThomannActions(
    var thomannId: String?,
    var isAccessible: Boolean?,
    var isJoinable: Boolean?,
    var isUpdatable: Boolean?
) {
    // Empty constructor is a must for Firestore
    constructor() : this(
        thomannId = null,
        isAccessible = null,
        isJoinable = null,
        isUpdatable = null
    )
}