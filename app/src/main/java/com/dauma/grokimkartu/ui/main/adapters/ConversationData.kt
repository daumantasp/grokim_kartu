package com.dauma.grokimkartu.ui.main.adapters

import com.dauma.grokimkartu.repositories.conversations.entities.Message

data class MyMessageConversationData(val message: Message)
data class PartnerMessageConversationData(val message: Message)
class MessageLastInPageData()