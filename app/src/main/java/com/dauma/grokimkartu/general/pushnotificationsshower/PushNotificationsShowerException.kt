package com.dauma.grokimkartu.general.pushnotificationsshower

class PushNotificationsShowerException(error: PushNotificationsShowerErrors)
    : Exception(error.toString()) {}


enum class PushNotificationsShowerErrors {
    CONTEXT_IS_NOT_SET
}