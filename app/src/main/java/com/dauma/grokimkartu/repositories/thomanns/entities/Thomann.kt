package com.dauma.grokimkartu.repositories.thomanns.entities

import com.google.firebase.Timestamp

data class Thomann(
    var id: String?,
    var name: String?,
    var city: String?,
    var isLocked: Boolean?,
    var creationDate: Timestamp?,
    var validUntil: Timestamp?
)