package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import java.net.URI
import java.util.concurrent.ExecutorService

class ReusableNetworkRequest(
    override val url: String,
    override val executor: ExecutorService,
    override val application: Application,
    override val httpMethod: NetworkRequestProvider.Method
) : NetworkRequest {

    private val cronetEngine = CronetEngine.Builder(application).build()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private val networkResponseRouter = NetworkResponseRouter(application.mainLooper, this)

    private var uploadDataProvider: UploadDataProvider? = null
    private var contentType: NetworkRequestProvider.ContentType? = null

    override fun addHeader(header: String, value: String) {
        headers[header] = value
    }

    override fun setUploadDataProvider(uploadDataProvider: UploadDataProvider, contentType: NetworkRequestProvider.ContentType) {
        this.uploadDataProvider = uploadDataProvider
        this.contentType = contentType
    }

    override fun obtainNetworkResponseRouter() : NetworkResponseRouter {
        return networkResponseRouter
    }

    override fun start(): UrlRequest {
        val urlRequest = getUrlRequestBuilder(NetworkRequestCallback(networkResponseRouter)).build()
        urlRequest.start()
        return urlRequest
    }

    private fun getUrlRequestBuilder(networkRequestCallback: NetworkRequestCallback) : UrlRequest.Builder {
        val logMessage = "Request built with Url: $url, Method: $httpMethod"

        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            networkRequestCallback,
            executor
        )

        if (uploadDataProvider != null) {
                requestBuilder.setUploadDataProvider(uploadDataProvider, executor)
                requestBuilder.addHeader("Content-Type", contentType?.contentType)
        }

        requestBuilder.setHttpMethod(httpMethod.method)

        CookieStore.getCookies(URI(url).host).forEach { _cookie ->
            Log.i("NetworkRequestProvider", "addCookie: $_cookie")
            requestBuilder.addHeader("Cookie", _cookie)
        }

        Log.i("NetworkRequestProvider", logMessage)

        return requestBuilder
    }
}