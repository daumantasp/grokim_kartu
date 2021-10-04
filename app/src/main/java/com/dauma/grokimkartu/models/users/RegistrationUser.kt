package com.dauma.grokimkartu.models.users

class RegistrationUser (
    val name: String,
    val email: String,
    val password: String,
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", "", "")
}