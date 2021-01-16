package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService

/**
 * The basic contract for network requests. Normally, a NetworkRequest is obtained from
 * the NetworkRequestProvider
 */
interface NetworkRequest {

    enum class Method(val method: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");
    }

    class ContentType private constructor(val contentType: String) {

        init {
            register(this)
        }

        private val separator = "; "

        fun addParam(name: String, value: String) : ContentType {
            val builder = StringBuilder()
            builder.append(contentType)
            builder.append(separator).append(name).append("=").append("\"$value\"")
            return ContentType(builder.toString())
        }

        override fun toString() : String {
            return contentType
        }

        companion object {
            private val contentTypes = HashMap<String, ContentType>()

            val APPLICATION_JSON = ContentType("application/json")
            val APPLICATION_URLENCODED = ContentType("application/x-www-form-urlencoded")
            val APPLICATION_PDF = ContentType("application/pdf")
            val MULTIPART = ContentType("multipart/form-data")

            private fun register(contentType: ContentType) {
                contentTypes[contentType.contentType] = contentType
            }

            fun find(type: String) : ContentType? {
                return contentTypes[type]
            }
        }
    }

    val url: String

    fun setOutputBuffer(outputBuffer: ByteBuffer)
    fun setOutputString(outputString: String) { setOutputBuffer(StandardCharsets.UTF_8.encode(outputString))}
    fun setContentType(contentType: ContentType)
    fun obtainNetworkResponseRouter() : NetworkResponseRouter

    fun start()
}