package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ConfigurableNetworkRequest private constructor(
    override val url: String,
    override val executor: ExecutorService,
    override val application: Application,
    override val httpMethod: NetworkRequestProvider.Method,
    private val networkResponseRouter: NetworkResponseRouter
) : NetworkRequest {

    constructor(
        url: String,
        executor: ExecutorService,
        application: Application,
        httpMethod: NetworkRequestProvider.Method
    ) : this(url, executor, application, httpMethod, NetworkResponseRouterImpl(application.mainLooper))

    private val cronetEngine = CronetEngine.Builder(application).build()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private var uploadDataProvider: UploadDataProvider? = null
    private var contentType: NetworkRequestProvider.ContentType? = null

    private var urlRequest: UrlRequest? = null

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

    @Throws(Exception::class)
    override fun start() {
        if (urlRequest != null) {
            throw Exception("You can't start this request twice")
        }
        urlRequest = getUrlRequestBuilder(NetworkRequestCallback(networkResponseRouter)).build().apply {
            start()
        }
    }

    override fun clone(): NetworkRequest {
        val clonedRequest = ConfigurableNetworkRequest(
            url,
            Executors.newSingleThreadExecutor(),
            application,
            httpMethod,
            (networkResponseRouter as NetworkResponseRouterImpl).clone()
        )
        headers.forEach { header ->
            clonedRequest.addHeader(header.key, header.value)
        }
        uploadDataProvider?.let { nonNullUploadProvider ->
            contentType?.let { nonNullContentType ->
                clonedRequest.setUploadDataProvider(nonNullUploadProvider,
                    nonNullContentType
                )
            }
        }
        return clonedRequest
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
            requestBuilder.addHeader("Cookie", _cookie)
        }

        Log.i("NetworkRequestProvider", logMessage)

        return requestBuilder
    }
}