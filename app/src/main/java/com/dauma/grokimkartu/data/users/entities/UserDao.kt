package com.dauma.grokimkartu.data.users.entities

class UserDao(
    var id: String,
    var visible: Boolean?
) {
    // Empty constructor is a must for Firestore
    constructor() : this("", null)
}