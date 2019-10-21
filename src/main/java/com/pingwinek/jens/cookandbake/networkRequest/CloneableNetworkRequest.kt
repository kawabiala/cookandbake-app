package com.pingwinek.jens.cookandbake.networkRequest

import android.app.Application
import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import java.net.URI
import java.util.concurrent.ExecutorService

class CloneableNetworkRequest(
    override val url: String,
    override val executor: ExecutorService,
    override val application: Application,
    override val httpMethod: NetworkRequestProvider.Method
) : NetworkRequest {

    private val cronetEngine = CronetEngine.Builder(application).build()

    private val headers: MutableMap<String, String> = mutableMapOf()
    private var networkResponseRouter: NetworkResponseRouter? = null
    private var uploadDataProvider: UploadDataProvider? = null
    private var contentType: NetworkRequestProvider.ContentType? = null

    override fun addHeader(header: String, value: String) {
        headers[header] = value
    }

    override fun setNetworkResponseRouter(networkResponseRouter: NetworkResponseRouter) {
        this.networkResponseRouter = networkResponseRouter
    }

    override fun setUploadDataProvider(uploadDataProvider: UploadDataProvider, contentType: NetworkRequestProvider.ContentType) {
        this.uploadDataProvider = uploadDataProvider
    }

    override fun optainNetworkResponseRouter() : NetworkResponseRouter {
        return NetworkResponseRouter(application.mainLooper)
    }

    override fun start() {
        networkResponseRouter?.let { router ->
            getUrlRequestBuilder(NetworkRequestCallback(router)).build().start()
        }
    }

    override fun clone() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getUrlRequestBuilder(networkRequestCallback: NetworkRequestCallback) : UrlRequest.Builder {
        var logMessage = "Request built with Url: $url, Method: $httpMethod"

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