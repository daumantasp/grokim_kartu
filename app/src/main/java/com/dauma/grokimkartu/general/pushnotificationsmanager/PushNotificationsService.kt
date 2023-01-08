package com.dauma.grokimkartu.general.pushnotificationsmanager

import android.util.Log
import com.dauma.grokimkartu.general.utils.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationsService : FirebaseMessagingService() {
    @Inject lateinit var utils: Utils
    @Inject lateinit var pushNotificationsManager: PushNotificationsManager

    companion object {
        private val TAG = "PushNotificationsService"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received: $message")
        message.notification?.let {
            pushNotificationsManager.showPushNotification(
                title = it.title ?: "",
                body = it.body ?: ""
            )
        }
    }
}