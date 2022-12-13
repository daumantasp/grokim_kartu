package com.dauma.grokimkartu.data.thomanns.entities

import java.sql.Timestamp

data class ThomannsRequest(
    val page: Int,
    val pageSize: Int,
    val cityId: Int? = null,
    var validUntil: Timestamp? = null,
    var isLocked: Boolean? = null
)
