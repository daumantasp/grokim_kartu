package com.dauma.grokimkartu.general.pushnotificationsmanager

class PushNotificationsManagerException(error: PushNotificationsManagerErrors)
    : Exception(error.toString()) {}


enum class PushNotificationsManagerErrors {
    CONTEXT_IS_NOT_SET,
    UNKNOWN
}