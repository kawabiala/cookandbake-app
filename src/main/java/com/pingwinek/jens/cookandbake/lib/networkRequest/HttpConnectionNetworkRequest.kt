package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

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

    override fun obtainNetworkResponseRouter(): NetworkResponseRouter {
        return networkResponseRouter
    }

    override fun start() {
        runBlocking {
            launch {
                doStart()
            }
        }
    }

    private suspend fun doStart() {
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

                val responseCode = connection.responseCode
                val message = readInput(connection.inputStream)


                networkResponseRouter.routeResponse(
                    AbstractNetworkResponseRoutes.Result.SUCCESS,
                    responseCode,
                    message
                )
            } catch (ioException: IOException) {
                val responseCode = connection.responseCode
                val message = readInput(connection.errorStream)

                networkResponseRouter.routeResponse(
                    AbstractNetworkResponseRoutes.Result.FAILED,
                    responseCode,
                    message
                )
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun writeOutput(outputBuffer: ByteBuffer, outputStream: BufferedOutputStream) {
        val channel = Channels.newChannel(outputStream)
        channel.write(outputBuffer)
        outputStream.close()
    }

    private fun readInput(inputStream: InputStream) : String {
        val reader = inputStream.bufferedReader(StandardCharsets.UTF_8)
        val msg: String
        reader.use {
            msg = it.readText()
        }
        return msg
    }
}