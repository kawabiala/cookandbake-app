package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.nio.charset.StandardCharsets

class NetworkResponse(
    private val connection: HttpURLConnection?
) {

    var code = -1

    init {
        try {
            if (connection != null) {
                code = connection.responseCode
                Log.i(this::class.java.name, "Response for url {$connection.url} - Code: $code")
            }
        } catch (ioException: IOException) {
            Log.i(this::class.java.name, "IOExeption while init: $ioException")
            connection?.disconnect()
        }
    }

    fun succeeded() : Boolean {
            return try {
                if (connection != null) {
                    connection.responseCode
                    true
                } else {
                    false
                }
            } catch (ioException: IOException) {
                Log.i(this::class.java.name, "IOExeption while succeeded: $ioException")
                connection?.disconnect()
                false
            }
    }

    /**
     * Closes the connection
     */
    fun responseAsString() : String? {
        var msg: String? = null
        try {
            val reader = connection?.inputStream?.bufferedReader(StandardCharsets.UTF_8)
            reader.use {
                msg = it?.readText()
            }
        } catch (ioException: IOException) {
            val reader = connection?.errorStream?.bufferedReader(StandardCharsets.UTF_8)
            reader.use {
                msg = it?.readText()
            }
        } finally {
            connection?.disconnect()
        }
        return msg
    }

    fun close() {
        try {
            connection?.disconnect()
        } catch (ioException: IOException) {
            // do nothing
        }
    }
}