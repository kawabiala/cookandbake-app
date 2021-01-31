package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import java.net.CookieHandler
import java.net.CookieManager
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService

class ConfigurableNetworkRequest private constructor(
    override val url: String,
    private val executor: ExecutorService,
    val application: Application,
    private val httpMethod: NetworkRequest.Method,
    private val networkResponseRouter: NetworkResponseRouter
) : NetworkRequest_old {

    constructor(
        url: String,
        executor: ExecutorService,
        application: Application,
        httpMethod: NetworkRequest.Method
    ) : this(url, executor, application, httpMethod, NetworkResponseRouterImpl(application.mainLooper))

    private val cronetEngine = CronetEngine.Builder(application).build()
    private var uploadDataProvider: UploadDataProvider? = null
    private var outputBuffer: ByteBuffer? = null
    private var contentType: String? = null

    private var urlRequest: UrlRequest? = null

    override fun setOutputBuffer(outputBuffer: ByteBuffer) {
        this.outputBuffer = outputBuffer
        this.uploadDataProvider = UploadDataProviders.create(outputBuffer)
    }

    override fun setContentType(contentType: NetworkRequest_old.ContentType) {
        this.contentType = contentType.toString()
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

    private fun getUrlRequestBuilder(networkRequestCallback: NetworkRequestCallback) : UrlRequest.Builder {
        var logMessage = "Request built with Url: $url, Method: $httpMethod"

        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            networkRequestCallback,
            executor
        )

        if (uploadDataProvider != null) {
                requestBuilder.setUploadDataProvider(uploadDataProvider, executor)
                requestBuilder.addHeader("Content-Type", contentType)
            logMessage = "$logMessage, UploadDataProvider: $uploadDataProvider, ContentType: $contentType"
        }

        requestBuilder.setHttpMethod(httpMethod.method)

        try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            cookieManager.cookieStore.get(URI(url)).forEach { cookie ->
                requestBuilder.addHeader("Cookie", cookie.toString())
            }
        } catch (classCastException: ClassCastException) {
            Log.i(this::class.java.name, "Could not create cookie headers due to exception: $classCastException")
        }

        Log.i("NetworkRequestProvider", logMessage)

        return requestBuilder
    }
}