package com.dauma.grokimkartu.data.thomanns.entities

data class ThomannsRequest(
    val page: Int,
    val pageSize: Int,
    val cityId: Int? = null,
    var validUntil: String? = null,
    var isLocked: Boolean? = null
)
