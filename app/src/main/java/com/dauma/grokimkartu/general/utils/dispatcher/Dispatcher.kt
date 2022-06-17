package com.dauma.grokimkartu.general.utils.dispatcher

interface Dispatcher {
    fun asyncNow(completion: () -> Unit)
    fun asyncAfterSeconds(seconds: Double, completion: () -> Unit)
    fun periodic(operationKey: String, period: Double, startImmediately: Boolean, repeats: Boolean, completion: () -> Unit)
    fun cancelPeriodic(key: String)
}