package com.dauma.grokimkartu.repositories.thomanns.entities

import com.google.firebase.Timestamp

data class Thomann(
    var id: String?,
    var userId: String?,
    var name: String?,
    var city: String?,
    var isLocked: Boolean?,
    var creationDate: Timestamp?,
    var validUntil: Timestamp?,
    var users: List<ThomannUser>?,
    val icon: ThomannPlayerIcon?
)