package com.dauma.grokimkartu.repositories.thomanns.entities

import java.sql.Timestamp

data class CreateThomann(
    var cityId: Int?,
    var validUntil: Timestamp?
)