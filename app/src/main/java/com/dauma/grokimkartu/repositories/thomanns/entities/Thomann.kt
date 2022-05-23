package com.dauma.grokimkartu.repositories.thomanns.entities

import java.sql.Timestamp

data class Thomann(
    var id: Int?,
    var user: ThomannUserConcise?,
    var city: String?,
    var isOwner: Boolean?,
    var isLocked: Boolean?,
    var isAccessible: Boolean?,
    var createdAt: Timestamp?,
    var validUntil: Timestamp?,
    var icon: ThomannPlayerIcon?
)