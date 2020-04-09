package com.pingwinek.jens.cookandbake.lib

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.pingwinek.jens.cookandbake.utils.SingletonHolder

class InternetConnectivityManager private constructor(application: Application) {

    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        val internetNetworkRequest = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        }.build()

        connectivityManager.registerNetworkCallback(internetNetworkRequest, networkCallback)
    }

    companion object : SingletonHolder<InternetConnectivityManager, Application>(::InternetConnectivityManager)
}