package com.dauma.grokimkartu.general.pushnotificationsmanager

import android.content.Context

interface PushNotificationsManager {
    fun withContext(context: Context)
    fun onTokenChanged(token: String)
    fun showPushNotification(title: String, body: String)
    fun arePushNotificationsSettingsEnabled(): PushNotificationsSettings
    fun subscribe(onComplete: (PushNotificationsManagerErrors?) -> Unit)
    fun unsubscribe(onComplete: (PushNotificationsManagerErrors?) -> Unit)
}