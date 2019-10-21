package com.pingwinek.jens.cookandbake.networkRequest

import android.app.Application
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import java.util.concurrent.ExecutorService

interface NetworkRequest {

    val url: String
    val application: Application
    val executor: ExecutorService
    val httpMethod: NetworkRequestProvider.Method

    fun addHeader(header: String, value: String)
    fun setNetworkResponseRouter(networkResponseRouter: NetworkResponseRouter)
    fun setUploadDataProvider(uploadDataProvider: UploadDataProvider, contentType: NetworkRequestProvider.ContentType)

    fun optainNetworkResponseRouter() : NetworkResponseRouter

    fun start()

    fun clone()
}