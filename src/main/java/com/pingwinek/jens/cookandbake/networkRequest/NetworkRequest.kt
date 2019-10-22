package com.pingwinek.jens.cookandbake.networkRequest

import android.app.Application
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import java.util.concurrent.ExecutorService

interface NetworkRequest {

    val url: String
    val application: Application
    val executor: ExecutorService
    val httpMethod: NetworkRequestProvider.Method

    fun addHeader(header: String, value: String)
    fun setUploadDataProvider(uploadDataProvider: UploadDataProvider, contentType: NetworkRequestProvider.ContentType)

    fun obtainNetworkResponseRouter() : NetworkResponseRouter

    fun start(): UrlRequest
}