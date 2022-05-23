package com.dauma.grokimkartu.repositories.profile.entities

import java.sql.Timestamp

data class Profile(
    var userId: Int?,
    var name: String?,
    var description: String?,
    var city: String?,
    var instrument: String?,
    var createdAt: Timestamp?
)