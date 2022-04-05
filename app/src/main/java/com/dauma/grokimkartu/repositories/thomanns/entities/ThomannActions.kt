package com.dauma.grokimkartu.repositories.thomanns.entities

data class ThomannActions(
    var thomannId: String?,
    var isAccessible: Boolean?,
    var isJoinable: Boolean?,
    var isLeavable: Boolean?,
    var isUpdatable: Boolean?
)