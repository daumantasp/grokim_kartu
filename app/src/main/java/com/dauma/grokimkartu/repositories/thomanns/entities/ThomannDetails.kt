package com.dauma.grokimkartu.repositories.thomanns.entities

import java.sql.Timestamp

data class ThomannDetails(
    var id: Int?,
    var user: ThomannUserConcise?,
    var city: String?,
    var isOwner: Boolean?,
    var isLocked: Boolean?,
    var createdAt: Timestamp?,
    var validUntil: Timestamp?,
    var users: ArrayList<ThomannUser>?,
    var totalAmount: String?,
    var actions: ArrayList<String>?
)
