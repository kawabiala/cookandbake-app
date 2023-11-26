package com.pingwinek.jens.cookandbake.sources

import android.os.ParcelFileDescriptor
import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.Multipart
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.InputStream

class FileManagerRemote(application: PingwinekCooksApplication) {

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val urlFiles = application.getURL(R.string.URL_FILES)

    suspend fun saveFile(inputStream: InputStream, type: String?): String?  {
        val contentType = type?.let { NetworkRequest.ContentType.find(it) } ?: return null
        val fileName = "recipe"

        return sendFile("$urlFiles/save", inputStream, fileName, contentType)
    }

    suspend fun changeFile(inputStream: InputStream, type: String?, name: String): String? {
        val contentType = type?.let { NetworkRequest.ContentType.find(it) } ?: return null

        return sendFile("$urlFiles/change", inputStream, name, contentType)
    }

    suspend fun load(fileName: String): ParcelFileDescriptor {
        //TODO write proper function load
        return ParcelFileDescriptor.open(File("NoIdeaWhatPath"), ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private suspend fun sendFile(
        url: String,
        inputStream: InputStream,
        fileName: String,
        contentType: NetworkRequest.ContentType
    ) : String? {

        val multipartBuilder = Multipart.Builder()
            .apply {
                addFile("file", fileName, contentType, inputStream)
            }
        val multipart = multipartBuilder.build()

        val networkRequest = networkRequestProvider.getNetworkRequest(url, NetworkRequest.Method.POST)
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
            Log.i(this::class.java.name, "Saving file failed with code ${response.code} and message ${response.responseAsString()}")
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