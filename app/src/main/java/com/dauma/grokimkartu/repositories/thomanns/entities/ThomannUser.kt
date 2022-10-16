package com.dauma.grokimkartu.repositories.thomanns.entities

import com.dauma.grokimkartu.general.IconLoader
import java.sql.Timestamp

data class ThomannUser(
    var id: Int?,
    var user: ThomannUserConcise?,
    var amount: String?,
    var createdAt: Timestamp?,
    var isCurrentUser: Boolean?,
    var actions: ArrayList<String>?,
    var iconLoader: IconLoader
)