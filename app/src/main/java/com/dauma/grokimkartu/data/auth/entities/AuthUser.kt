package com.dauma.grokimkartu.data.auth.entities

import android.net.Uri

data class AuthUser(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)