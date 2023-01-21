package com.dauma.grokimkartu.general.pushnotificationsshower

import android.content.Context

interface PushNotificationsShower {
    fun withContext(context: Context)
    fun showPushNotification(title: String, body: String)
}