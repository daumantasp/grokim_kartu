package com.dauma.grokimkartu.data.thomanns.entities

import com.google.firebase.Timestamp

data class ThomannDao(
    var id: String?,
    var userId: String?,
    var name: String?,
    var city: String?,
    var isLocked: Boolean?,
    var creationDate: Timestamp?,
    var validUntil: Timestamp?
)