package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.Executors

/**
 * This is usually the starting point for network requests
 */
class NetworkRequestProvider private constructor(val application: Application) {

    fun getNetworkRequest(
        url: String,
        method: NetworkRequest.Method?
    ): NetworkRequest {
        return getHttpNetworkRequest(
            url,
            method ?: NetworkRequest.Method.GET
        )
    }

    private fun getConfigurableNetworkRequest(
        url: String,
        method: NetworkRequest.Method?
    ): NetworkRequest {
        return ConfigurableNetworkRequest(
            url,
            Executors.newSingleThreadExecutor(),
            application,
            method ?: NetworkRequest.Method.GET
        )
    }

    private fun getCronetEngineNetworkRequest(
        url: String,
        method: NetworkRequest.Method?
    ): CronetEngineNetworkRequest {
        return CronetEngineNetworkRequest(
            url,
            application,
            method ?: NetworkRequest.Method.GET
        )
    }

    private fun getHttpNetworkRequest(
        url: String,
        method: NetworkRequest.Method?
    ): HttpConnectionNetworkRequest {
        return HttpConnectionNetworkRequest(
            url,
            application,
            method ?: NetworkRequest.Method.GET
        )
    }

    companion object :
        SingletonHolder<NetworkRequestProvider, Application>(::NetworkRequestProvider) {

        fun toBody(params: Map<String, String>): String {

            val bodyBuilder = StringBuilder()
            var first = true

            params.forEach { entry: Map.Entry<String, String> ->
                if (first) {
                    first = false
                } else {
                    bodyBuilder.append("&")
                }

                bodyBuilder.append(entry.key)
                bodyBuilder.append("=")
                bodyBuilder.append(URLEncoder.encode(entry.value, "UTF-8"))
            }

            return bodyBuilder.toString()
        }

        fun toJsonBody(params: Map<String, String>): String {
            return JSONObject(params).toString()
        }

    }

}