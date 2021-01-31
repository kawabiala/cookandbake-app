package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.Channels

class HttpConnectionNetworkRequest private constructor(
    override val url: String,
    val application: Application,
    private val httpMethod: NetworkRequest.Method,
    private val networkResponseRouter: NetworkResponseRouter
) : NetworkRequest {

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

    private var outputBuffer: ByteBuffer? = null
    private var contentType: String? = null

    override fun setOutputBuffer(outputBuffer: ByteBuffer) {
        this.outputBuffer = outputBuffer
    }

    override fun setContentType(contentType: NetworkRequest.ContentType) {
        this.contentType = contentType.toString()
    }

    override suspend fun start() : NetworkResponse {
        return doStart()
    }

    private suspend fun doStart() : NetworkResponse {
        var response: NetworkResponse? = null
        withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = httpMethod.method
            connection.addRequestProperty("Accept-Encoding", "identity")

            contentType?.let {
                connection.addRequestProperty("Content-Type", contentType)
            }

            try {
                outputBuffer?.let {
                    connection.doOutput = true
                    val outputStream = BufferedOutputStream(connection.outputStream)
                    writeOutput(it, outputStream)
                }

                response = NetworkResponse(connection)
            } catch (ioException: IOException) {
                response = NetworkResponse(connection)
            } finally {
                connection.disconnect()
            }
        }

        return response ?: NetworkResponse(null)
    }

    private fun writeOutput(outputBuffer: ByteBuffer, outputStream: BufferedOutputStream) {
        val channel = Channels.newChannel(outputStream)
        channel.write(outputBuffer)
        outputStream.close()
    }
}