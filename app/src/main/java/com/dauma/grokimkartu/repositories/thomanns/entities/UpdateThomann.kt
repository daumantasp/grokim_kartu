package com.dauma.grokimkartu.repositories.thomanns.entities

import java.sql.Timestamp

data class UpdateThomann(
    var isLocked: Boolean?,
    var cityId: Int?,
    var validUntil: Timestamp?
)