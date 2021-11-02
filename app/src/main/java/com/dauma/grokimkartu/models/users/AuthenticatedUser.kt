package com.dauma.grokimkartu.models.users

import android.net.Uri

data class AuthenticatedUser(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)