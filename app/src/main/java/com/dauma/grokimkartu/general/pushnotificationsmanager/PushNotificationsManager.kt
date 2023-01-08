package com.dauma.grokimkartu.general.pushnotificationsmanager

import android.content.Context

interface PushNotificationsManager {
    fun withContext(context: Context)
    fun showPushNotification(title: String, body: String)
}