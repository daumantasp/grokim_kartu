package com.dauma.grokimkartu.repositories.thomanns

import java.sql.Timestamp

class ThomannsFilter(
    val cityId: Int?,
    val validUntil: Timestamp?,
    val isLocked: Boolean?
) {
    companion object {
        val CLEAR = ThomannsFilter(
            cityId = null,
            validUntil = null,
            isLocked = null
        )
    }
}