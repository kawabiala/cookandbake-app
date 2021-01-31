package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import java.net.CookieHandler
import java.net.CookieManager
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class CronetEngineNetworkRequest private constructor(
    override val url: String,
    val application: Application,
    private val httpMethod: NetworkRequest.Method,
    private val networkResponseRouter: NetworkResponseRouter
    ) : NetworkRequest_old {

    constructor(
        url: String,
        application: Application,
        httpMethod: NetworkRequest.Method
    ) : this(
        url,
        application,
        httpMethod,
        NetworkResponseRouterImpl(application.mainLooper)
    )

    private val executorService = Executors.newSingleThreadExecutor()
    private val cronetEngine = CronetEngine.Builder(application).build()
    private val requestBuilder = cronetEngine.newUrlRequestBuilder(
        url,
        NetworkRequestCallback(networkResponseRouter),
        executorService)

    init {
        requestBuilder.setHttpMethod(httpMethod.method)

        try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            cookieManager.cookieStore.get(URI(url)).forEach { cookie ->
                requestBuilder.addHeader("Cookie", cookie.toString())
            }
        } catch (classCastException: ClassCastException) {
            Log.i(this::class.java.name, "Could not create cookie headers due to exception: $classCastException")
        }
    }

    private var urlRequest: UrlRequest? = null

    override fun setOutputBuffer(outputBuffer: ByteBuffer) {
        val uploadDataProvider = UploadDataProviders.create(outputBuffer)
        requestBuilder.setUploadDataProvider(uploadDataProvider, executorService)
    }

    override fun setContentType(contentType: NetworkRequest_old.ContentType) {
        requestBuilder.addHeader("Content-Type", contentType.toString())
    }

    override fun obtainNetworkResponseRouter(): NetworkResponseRouter {
        return networkResponseRouter
    }

    override fun start() {
        if (urlRequest != null) {
            throw Exception("You can't start this request twice")
        }
        urlRequest = requestBuilder.build().apply {
            start()
        }
    }
}