package com.dauma.grokimkartu.repositories.users.entities

import android.graphics.Bitmap

data class Profile(
    var name: String?,
    var instrument: String?,
    var description: String?,
    var photo: Bitmap?,
    var city: String?
)