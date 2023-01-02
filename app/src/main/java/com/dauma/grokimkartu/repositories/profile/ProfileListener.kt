package com.dauma.grokimkartu.repositories.profile

interface ProfileListener {
    fun notificationsCountChanged()
    fun privateConversationsCountChanged()
    fun thomannConversationsCountChanged()
}