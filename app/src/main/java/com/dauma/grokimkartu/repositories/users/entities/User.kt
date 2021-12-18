package com.dauma.grokimkartu.repositories.users.entities

import android.graphics.Bitmap
import com.google.firebase.Timestamp

data class User(
    val providerId: String?,
    val id: String?,
    val name: String?,
    val email: String?,
    val photo: Bitmap?,
    val visible: Boolean?,
    val registrationDate: Timestamp?
)