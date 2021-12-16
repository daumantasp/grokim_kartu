package com.dauma.grokimkartu.repositories.users.entities

import android.net.Uri
import com.google.firebase.Timestamp

data class User(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?,
    val visible: Boolean?,
    val registrationDate: Timestamp?
)