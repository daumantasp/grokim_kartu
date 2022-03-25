package com.dauma.grokimkartu.repositories.thomanns.entities

import com.google.firebase.Timestamp

data class ThomannUser(
    var userId: String?,
    var userName: String?,
    var thomannId: String?,
    var amount: Double?,
    var isCurrentUser: Boolean?,
    var isUserCreator: Boolean?,
    val icon: ThomannPlayerIcon?,
    val joinDate: Timestamp?
)