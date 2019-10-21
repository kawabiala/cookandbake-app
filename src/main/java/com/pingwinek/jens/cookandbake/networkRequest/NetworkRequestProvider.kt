package com.pingwinek.jens.cookandbake.networkRequest

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.SingletonHolder
import org.chromium.net.*
import org.json.JSONObject
import java.net.URI
import java.net.URLEncoder
import java.util.concurrent.Executors

class NetworkRequestProvider private constructor(val application: Application){

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

    private val cronetEngine = CronetEngine.Builder(application).build()

    fun getNetworkRequest(
        url: String,
        method: Method?
    ) : NetworkRequest {
        return CloneableNetworkRequest(url, Executors.newSingleThreadExecutor(), application, method ?: Method.GET)
    }

    fun runRequest(
        url: String,
        method: Method?,
        contentType: ContentType?,
        params: Map<String, String>?,
        networkResponseRouter: NetworkResponseRouter
    ) {

        val request = getRequestBuilder(url, method, contentType, params,
            NetworkRequestCallback(networkResponseRouter)
        ).build()

        request.start()
    }

    private fun getRequestBuilder(
        url: String,
        method: Method?,
        contentType: ContentType?,
        params: Map<String, String>?,
        networkRequestCallback: NetworkRequestCallback
    ): UrlRequest.Builder {

        var logMessage = "Request built with Url: $url, Method: $method"

        val executor = Executors.newSingleThreadExecutor()
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            networkRequestCallback,
            executor
        )

        if (params != null) {
            if (contentType == ContentType.APPLICATION_URLENCODED) {
                val body = toBody(params)

                requestBuilder.setUploadDataProvider(NetworkRequestBodyProvider(body), executor)
                requestBuilder.addHeader("Content-Type", ContentType.APPLICATION_URLENCODED.contentType)

                logMessage += " and Body: $body"
            } else if (contentType == ContentType.APPLICATION_JSON) {
                val body = toJsonBody(params)

                requestBuilder.setUploadDataProvider(NetworkRequestBodyProvider(body), executor)
                requestBuilder.addHeader("Content-Type", ContentType.APPLICATION_JSON.contentType)

                logMessage += " and Body: $body"
            }
        }

        requestBuilder.setHttpMethod(method?.method ?: Method.GET.method)

        CookieStore.getCookies(URI(url).host).forEach { _cookie ->
            Log.i("NetworkRequestProvider", "addCookie: $_cookie")
            requestBuilder.addHeader("Cookie", _cookie)
        }

        Log.i("NetworkRequestProvider", logMessage)

        return requestBuilder
    }

    fun obtainNetworkRequestRouter() : NetworkResponseRouter {
        return NetworkResponseRouter(application.mainLooper)
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

    companion object : SingletonHolder<NetworkRequestProvider, Application>(::NetworkRequestProvider)

}