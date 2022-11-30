package com.dauma.grokimkartu.general.networkchangereceiver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build

class NetworkChangeReceiverImpl(private val context: Context) : NetworkChangeReceiver {
    private var listeners: MutableList<NetworkChangeListener> = mutableListOf()
    private var connectivityManager: ConnectivityManager? = null

    init {
        setConnectionManager()
    }

    override fun addListener(listener: NetworkChangeListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: NetworkChangeListener) {
        listeners.remove(listener)
    }

    override fun isNetworkAvailable(): Boolean {
        if (connectivityManager == null) {
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        return connectivityManager!!.activeNetwork != null
    }

    private fun setConnectionManager() {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                for (listener in listeners) {
                    listener.onNetworkAvailable()
                }
                super.onAvailable(network)
            }

            override fun onLost(network: Network) {
                for (listener in listeners) {
                    listener.onNetworkLost()
                }
                super.onLost(network)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager!!.registerDefaultNetworkCallback(connectivityManagerCallback)
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager!!.registerNetworkCallback(networkRequest, connectivityManagerCallback)
        }
    }
}