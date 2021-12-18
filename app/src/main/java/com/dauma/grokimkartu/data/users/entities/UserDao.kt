package com.dauma.grokimkartu.data.users.entities

import com.google.firebase.Timestamp

data class UserDao(
    var id: String?,
    var name: String?,
    var visible: Boolean?,
    var registrationDate: Timestamp?
)