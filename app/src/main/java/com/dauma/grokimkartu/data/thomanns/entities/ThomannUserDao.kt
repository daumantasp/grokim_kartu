package com.dauma.grokimkartu.data.thomanns.entities

import com.google.firebase.Timestamp

data class ThomannUserDao(
    var userId: String?,
    var userName: String?,
    var thomannId: String?,
    var amount: Double?,
    var joinDate: Timestamp?
)
