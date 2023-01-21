package com.dauma.grokimkartu.general.pushnotificationsmanager

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.dauma.grokimkartu.data.settings.SettingsDao
import com.dauma.grokimkartu.data.settings.SettingsDaoResponseStatus
import com.dauma.grokimkartu.data.settings.entities.DeletePushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.PushNotificationsTokenRequest
import com.dauma.grokimkartu.general.pushnotificationsshower.PushNotificationsShower
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.google.firebase.messaging.FirebaseMessaging

class PushNotificationsManagerImpl(
    private val pushNotificationsShower: PushNotificationsShower,
    private val settingsDao: SettingsDao,
    private val user: User,
    private val utils: Utils
): PushNotificationsManager, LoginListener {
    private var context: Context? = null

    companion object {
        private const val PUSH_NOTIFICATIONS_ARE_SUBSCRIBED = "PUSH_NOTIFICATIONS_ARE_SUBSCRIBED"
    }

    init {
        addOnMoveToForegroundObserver()
    }

    private fun addOnMoveToForegroundObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_START && user.isUserLoggedIn()) {
                    if (arePushNotificationsSettingsEnabled() == PushNotificationsSettings.ENABLED_AND_SUBSCRIBED) {
                        getToken { token, _ ->
                            updateToken(PushNotificationsTokenRequest(token, utils.appUtils.deviceId())) { _ -> }
                        }
                    } else {
                        deleteToken { _ -> }
                    }
                }
            }
        })
    }

    override fun withContext(context: Context) {
        this.context = context
        pushNotificationsShower.withContext(context)
    }

    override fun onTokenChanged(token: String) {
        if (user.isUserLoggedIn() && arePushNotificationsSettingsEnabled() == PushNotificationsSettings.ENABLED_AND_SUBSCRIBED) {
            updateToken(PushNotificationsTokenRequest(token, utils.appUtils.deviceId()))
        }
    }

    override fun showPushNotification(title: String, body: String) {
        pushNotificationsShower.showPushNotification(title, body)
    }

    override fun arePushNotificationsSettingsEnabled(): PushNotificationsSettings {
        context?.let {
            val areNotificationsEnabled = NotificationManagerCompat.from(it).areNotificationsEnabled()
            val areSubscribed = utils.sharedStorageUtils.getEntry(PUSH_NOTIFICATIONS_ARE_SUBSCRIBED) ?: "false"
            if (areNotificationsEnabled && areSubscribed == "true") {
                return PushNotificationsSettings.ENABLED_AND_SUBSCRIBED
            } else if (areNotificationsEnabled && areSubscribed == "false") {
                return PushNotificationsSettings.ENABLED_NOT_SUBSCRIBED
            } else {
                return PushNotificationsSettings.DISABLED
            }
        } ?: throw PushNotificationsManagerException(PushNotificationsManagerErrors.CONTEXT_IS_NOT_SET)
    }

    override fun subscribe(onComplete: (PushNotificationsManagerErrors?) -> Unit) {
        utils.sharedStorageUtils.save(PUSH_NOTIFICATIONS_ARE_SUBSCRIBED, "true")
        getToken { token, pushNotificationsManagerErrors ->
            if (user.isUserLoggedIn() && arePushNotificationsSettingsEnabled() == PushNotificationsSettings.ENABLED_AND_SUBSCRIBED) {
                updateToken(PushNotificationsTokenRequest(token, utils.appUtils.deviceId())) { _ ->
                    onComplete(pushNotificationsManagerErrors)
                }
            } else {
                onComplete(pushNotificationsManagerErrors)
            }
        }
    }

    override fun unsubscribe(onComplete: (PushNotificationsManagerErrors?) -> Unit) {
        utils.sharedStorageUtils.save(PUSH_NOTIFICATIONS_ARE_SUBSCRIBED, "false")
        FirebaseMessaging.getInstance().deleteToken()
        if (user.isUserLoggedIn()) {
            deleteToken { _ ->
                onComplete(null)
            }
        }
    }

    private fun getToken(onComplete: (String?, PushNotificationsManagerErrors?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(it.result, null)
                } else {
                    onComplete(null, PushNotificationsManagerErrors.UNKNOWN)
                }
            }
            .addOnFailureListener {
                onComplete(null, PushNotificationsManagerErrors.UNKNOWN)
            }
            .addOnCanceledListener {
                onComplete(null, PushNotificationsManagerErrors.UNKNOWN)
            }
    }

    // MARK: LoginListener
    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            if (arePushNotificationsSettingsEnabled() == PushNotificationsSettings.ENABLED_AND_SUBSCRIBED) {
                getToken { token, _ ->
                    updateToken(PushNotificationsTokenRequest(token, utils.appUtils.deviceId())) { _ -> }
                }
            } else {
                deleteToken { _ -> }
            }
        }
    }

    private fun updateToken(
        pushNotificationsTokenRequest: PushNotificationsTokenRequest,
        onComplete: (SettingsDaoResponseStatus) -> Unit = {}) {
        settingsDao.pushNotificationsToken(
            pushNotificationsTokenRequest = pushNotificationsTokenRequest,
            accessToken = user.getBearerAccessToken()!!,
            onComplete = onComplete
        )
    }

    private fun deleteToken(onComplete: (SettingsDaoResponseStatus) -> Unit = {}) {
        settingsDao.deletePushNotificationsToken(
            deletePushNotificationsTokenRequest = DeletePushNotificationsTokenRequest(utils.appUtils.deviceId()),
            accessToken = user.getBearerAccessToken()!!,
            onComplete = onComplete
        )
    }
}

