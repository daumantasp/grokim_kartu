package com.dauma.grokimkartu.repositories.conversations.entities

import com.dauma.grokimkartu.general.IconLoader

data class ConversationPartner(
    var id: Int?,
    var name: String?,
    var iconLoader: IconLoader?
)