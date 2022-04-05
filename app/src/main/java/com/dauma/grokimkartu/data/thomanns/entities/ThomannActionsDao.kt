package com.dauma.grokimkartu.data.thomanns.entities

data class ThomannActionsDao(
    var thomannId: String?,
    var isAccessible: Boolean?,
    var isJoinable: Boolean?,
    var isLeavable: Boolean?,
    var isUpdatable: Boolean?
)