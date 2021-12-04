package com.dauma.grokimkartu.repositories.users.entities

import android.net.Uri

data class User(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?,
    val visible: Boolean?
)