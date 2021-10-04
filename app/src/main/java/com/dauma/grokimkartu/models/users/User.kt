package com.dauma.grokimkartu.models.users

class User(
    var id: String,
    val name: String
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", "")
}