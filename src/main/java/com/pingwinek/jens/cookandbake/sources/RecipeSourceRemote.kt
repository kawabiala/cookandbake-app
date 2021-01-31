package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkResponse
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.models.Recipes
import java.util.*

/**
 * Source to retrieve and manipulate recipes from remote source
 *
 * @property NetworkRequestProvider instance of [NetworkRequestProvider]
 */
class RecipeSourceRemote constructor(
    private val networkRequestProvider: NetworkRequestProvider,
    private val authService: AuthService,
    application: PingwinekCooksApplication
) : RecipeSource<RecipeRemote> {

    private val urlRecipe = application.getURL(R.string.URL_RECIPE)

    override suspend fun getAll() : LinkedList<RecipeRemote> {
        if (!authService.ensureSession()) return LinkedList()

        val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.GET)
        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            parseRecipes(response) ?: LinkedList()
        } else {
            LinkedList()
        }
    }

    override suspend fun get(id: Int) : RecipeRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest("$urlRecipe/$id", NetworkRequest.Method.GET)
        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            val recipes = parseRecipes(response)
            return recipes?.let { parseRecipe(it) }
        } else {
            null
        }
    }

    override suspend fun new(item: RecipeRemote) : RecipeRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.PUT)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }

        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            val recipes = parseRecipes(response)
            return recipes?.let { parseRecipe(it) }
        } else {
            null
        }
    }

    override suspend fun update(item: RecipeRemote) : RecipeRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.POST)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }

        val response = networkRequest.start()
        return if (response.succeeded() && response.code == 200) {
            val recipes = parseRecipes(response)
            return recipes?.let { parseRecipe(it) }
        } else {
            null
        }
    }

    override suspend fun delete(id: Int) : Boolean {
        if (!authService.ensureSession()) return false

        val networkRequest = networkRequestProvider.getNetworkRequest("$urlRecipe/$id", NetworkRequest.Method.DELETE)
        val response = networkRequest.start()
        return response.succeeded() && response.code == 200
    }

    private fun parseRecipes(response: NetworkResponse) : Recipes? {
        val responseString = response.responseAsString() ?: return null
        return Recipes(responseString)
    }

    private fun parseRecipe(recipes: Recipes) : RecipeRemote? {
        return if (recipes.isEmpty()) {
            null
        } else {
            recipes[0]
        }
    }
}