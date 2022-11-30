package com.dauma.grokimkartu.general.networkchangereceiver

interface NetworkChangeListener {
    fun onNetworkAvailable()
    fun onNetworkLost()
}