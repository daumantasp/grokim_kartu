package com.dauma.grokimkartu.data.users.entities

import android.graphics.Bitmap

data class ProfileDao(
    var name: String?,
    var instrument: String?,
    var description: String?,
    var photo: Bitmap?,
    var city: String?
)