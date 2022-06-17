package com.dauma.grokimkartu.general.utils.dispatcher

interface DispatcherUtils {
    val isInForeground: Boolean
    val main: Dispatcher
    val bg: Dispatcher
    fun appMovedToForeground(isInForeground: Boolean)
}