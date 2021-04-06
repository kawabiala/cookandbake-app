package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkResponse
import com.pingwinek.jens.cookandbake.models.FileRemote
import com.pingwinek.jens.cookandbake.models.Files
import java.util.*

class FileSourceRemote(
    private val networkRequestProvider: NetworkRequestProvider,
    private val authService: AuthService,
    application: PingwinekCooksApplication
) : FileSource<FileRemote> {

    private val urlRecipe = application.getURL(R.string.URL_RECIPE)
    private val urlFile = application.getURL(R.string.URL_FILES)

    override suspend fun getAllForEntityId(recipeId: Int): LinkedList<FileRemote> {
        if (!authService.ensureSession()) return LinkedList()

        val networkRequest = networkRequestProvider.getNetworkRequest("$urlRecipe/$recipeId/files/", NetworkRequest.Method.GET)
        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            parseFiles(response) ?: LinkedList()
        } else {
            LinkedList<FileRemote>()
        }
    }

    override suspend fun getAll(): LinkedList<FileRemote> {
        if (!authService.ensureSession()) return LinkedList()

        val networkRequest = networkRequestProvider.getNetworkRequest(urlFile, NetworkRequest.Method.GET)
        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            parseFiles(response) ?: LinkedList()
        } else {
            LinkedList<FileRemote>()
        }
    }

    override suspend fun get(id: Int): FileRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest("$urlFile/$id", NetworkRequest.Method.GET)
        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            parseFile(response)
        } else {
            null
        }
    }

    override suspend fun new(item: FileRemote): FileRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlFile, NetworkRequest.Method.PUT)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }
        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            parseFile(response)
        } else {
            null
        }
    }

    override suspend fun update(item: FileRemote): FileRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlFile, NetworkRequest.Method.POST)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }
        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            parseFile(response)
        } else {
            null
        }
    }

    override suspend fun delete(id: Int): Boolean {
        if (!authService.ensureSession()) return false

        val networkRequest = networkRequestProvider.getNetworkRequest("$urlFile/$id", NetworkRequest.Method.DELETE)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(emptyMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }
        val response = networkRequest.start()
        return (response.succeeded() && response.code == 200)
    }

    private fun parseFiles(response: NetworkResponse) : Files? {
        val responseString = response.responseAsString() ?: return null
        return Files(responseString)
    }

    private fun parseFile(response: NetworkResponse) : FileRemote? {
        val files = parseFiles(response)
        return if (files == null || files.isEmpty()) {
            null
        } else {
            files[0]
        }
    }
}