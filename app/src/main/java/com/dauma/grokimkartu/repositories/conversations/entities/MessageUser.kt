package com.dauma.grokimkartu.repositories.conversations.entities

import com.dauma.grokimkartu.general.IconLoader

data class MessageUser(
    var id: Int?,
    var name: String?,
    var isCurrent: Boolean?,
    var iconLoader: IconLoader?
)
