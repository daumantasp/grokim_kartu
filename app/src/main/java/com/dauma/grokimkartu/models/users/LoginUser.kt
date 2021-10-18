package com.dauma.grokimkartu.models.users

class LoginUser(
    val email: String,
    val password: String,
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", "")
}