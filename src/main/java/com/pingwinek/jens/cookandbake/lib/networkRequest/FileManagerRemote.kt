package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream

class FileManagerRemote(application: PingwinekCooksApplication) {

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val urlFiles = application.getURL(R.string.URL_FILES)

    suspend fun saveFile(inputStream: InputStream, type: String?, name: String?): String?  {
        val contentType = type?.let { NetworkRequest.ContentType.find(it) } ?: return null
        val fileName = name ?: "recipe"
        val method = if (fileName == "recipe") {
            NetworkRequest.Method.POST
        } else {
            NetworkRequest.Method.PUT
        }

        val multipartBuilder = Multipart.Builder()
            .apply {
                addFile("file", fileName, contentType, inputStream)
            }
        val multipart = multipartBuilder.build()

        return saveFile(multipart, method)
    }

    private suspend fun saveFile(multipart: Multipart, method: NetworkRequest.Method) : String? {
        val networkRequest = networkRequestProvider.getNetworkRequest(urlFiles, method)
            .apply {
                setOutputBuffer(multipart.getContent())
                setContentType(multipart.getContentType())
            }

        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            response.responseAsString()?.let {
                parseFileManagerResponse(it)
            }
        } else {
            null
        }
    }

    private fun parseFileManagerResponse(response: String) : String? {
        val key = "file_id"

        val responseObject: JSONObject = try {
            JSONObject(response)
        } catch (jsonException: JSONException) {
            Log.e(this::class.java.name, "Could not parse $response to json because of JSONException: ${jsonException.message}")
            null
        } ?: return null

        return if (responseObject.isNull(key)) {
            Log.e(this::class.java.name, "saveFile successful, but no uri returned")
            null
        } else {
            Log.i(this::class.java.name, "JSONObject: $responseObject")
            responseObject.getString(key)
        }
    }

    companion object {
        val TYPES = mapOf(
            Pair("application/pdf", "pdf")
        )
    }
}