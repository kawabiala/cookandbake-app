package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import org.chromium.net.UploadDataProvider
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.Executors

class NetworkRequestProvider private constructor(val application: Application) {

    enum class Method(val method: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE")
    }

    enum class ContentType(val contentType: String) {
        APPLICATION_JSON("application/json"),
        APPLICATION_URLENCODED("application/x-www-form-urlencoded")
    }

    fun getNetworkRequest(
        url: String,
        method: Method?
    ): NetworkRequest {
        return ReusableNetworkRequest(
            url,
            Executors.newSingleThreadExecutor(),
            application,
            method ?: Method.GET
        )
    }

    companion object :
        SingletonHolder<NetworkRequestProvider, Application>(::NetworkRequestProvider) {

        fun getUploadDataProvider(
            params: Map<String, String>,
            contentType: ContentType
        ): UploadDataProvider {
            return when (contentType) {
                ContentType.APPLICATION_URLENCODED -> {
                    NetworkRequestBodyProvider(toBody(params))
                }
                ContentType.APPLICATION_JSON -> {
                    NetworkRequestBodyProvider(toJsonBody(params))
                }
            }
        }

        private fun toBody(params: Map<String, String>): String {

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

        private fun toJsonBody(params: Map<String, String>): String {
            return JSONObject(params).toString()
        }

    }

}