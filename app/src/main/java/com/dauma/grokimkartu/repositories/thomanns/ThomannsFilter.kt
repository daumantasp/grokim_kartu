package com.dauma.grokimkartu.repositories.thomanns

class ThomannsFilter(
    val cityId: Int?,
    val validUntil: String?,
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