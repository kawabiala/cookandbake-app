package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.net.Uri
import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.InputStream

class FileManagerRemote(application: PingwinekCooksApplication) {

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val urlFiles = application.getURL(R.string.URL_FILES)

    fun saveFile(inputStream: InputStream, type: String?, name: String?): Promise<String>  {
        val promise = Promise<String>()

        val contentType = type?.let { NetworkRequest.ContentType.find(it) } ?: return promise
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

        saveFile(multipart, method).setResultHandler {
            promise.setResult(it.status, it.value)
        }

        return promise
    }

    private fun saveFile(multipart: Multipart, method: NetworkRequest.Method) : Promise<String> {
        val promise = Promise<String>()

        val networkRequest = networkRequestProvider.getNetworkRequest(urlFiles, method)
            .apply {
                setOutputBuffer(multipart.getContent())
                setContentType(multipart.getContentType())
            }

        networkRequest.obtainNetworkResponseRouter()
            .apply {
                registerDefaultFailedRoute { _, code, _ ->
                    Log.e(this::class.java.name, "saveFile failed with code $code")
                    promise.setResult(Promise.Status.FAILURE, null)
                }
                registerDefaultSuccessRoute { _, code, _ ->
                    Log.e(this::class.java.name, "saveFile failed with status 'success' and code $code")
                    promise.setResult(Promise.Status.FAILURE, null)
                }
                registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 200) { _, _, response->
                    val result = parseFileManagerResponse(response)
                    result?.let {
                        promise.setResult(Promise.Status.SUCCESS, it)
                    } ?: promise.setResult(Promise.Status.FAILURE, null)
                }
            }

        networkRequest.start()

        return promise
    }

    fun getFile(uri: Uri) : Promise<File> {
        val promise = Promise<File>()

        val networkRequest = networkRequestProvider.getNetworkRequest(uri.toString(), NetworkRequest.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerDefaultFailedRoute { _, code, _ ->
            Log.e(this::class.java.name, "getFile failed with code $code")
            promise.setResult(Promise.Status.FAILURE, null)
        }

        networkResponseRouter.registerDefaultSuccessRoute { _, code, _ ->
            Log.e(this::class.java.name, "getFile failed with status 'success' and code $code")
            promise.setResult(Promise.Status.FAILURE, null)
        }

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 200) { _, _, response ->
           // promise.setResult(Promise.Status.SUCCESS, //TODO)
        }

        networkRequest.start()

        return promise
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