package com.dauma.grokimkartu.general.networkchangereceiver

interface NetworkChangeReceiver {
    fun addListener(listener: NetworkChangeListener)
    fun removeListener(listener: NetworkChangeListener)
    fun isNetworkAvailable(): Boolean
}