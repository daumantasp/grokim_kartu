package com.dauma.grokimkartu.data.users.entities

import android.net.Uri

data class AuthenticationUser(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)