package com.dauma.grokimkartu.general.pushnotificationsmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.ui.MainActivity
import kotlin.random.Random

class PushNotificationsManagerImpl: PushNotificationsManager {
    private var context: Context? = null

    companion object {
        private const val PUSH_NOTIFICATIONS_CHANNEL_ID = "PUSH_NOTIFICATIONS_CHANNEL_ID"
    }

    override fun withContext(context: Context) {
        this.context = context
    }

    override fun showPushNotification(title: String, body: String) {
        context?.let {
            val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createPushNotificationsChannel(notificationManager)
            }

            val intent = Intent(it, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setAction(Intent.ACTION_MAIN)
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(it, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(it, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notification = NotificationCompat.Builder(it, PUSH_NOTIFICATIONS_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_profile)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPushNotificationsChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            PUSH_NOTIFICATIONS_CHANNEL_ID,
            "Push Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Push Notifications"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}