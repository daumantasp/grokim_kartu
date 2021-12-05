package com.dauma.grokimkartu.data.users.entities

class ProfileDao(
    var instrument: String,
    var description: String?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", null)
}