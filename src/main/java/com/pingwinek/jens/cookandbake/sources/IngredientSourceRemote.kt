package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkResponse
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.Ingredients
import java.util.*

/**
 * Source for retrieving and manipulating ingredients in a remote database
 *
 * @property networkRequestProvider instance of [NetworkRequestProvider]
 */
class IngredientSourceRemote constructor(
    private val networkRequestProvider: NetworkRequestProvider,
    private val authService: AuthService,
    application: PingwinekCooksApplication
) : IngredientSource<IngredientRemote> {

    private val urlRecipe = application.getURL(R.string.URL_RECIPE)
    private val urlIngredient = application.getURL(R.string.URL_INGREDIENT)

    override suspend fun getAll() : LinkedList<IngredientRemote> {
        if (!authService.ensureSession()) return LinkedList()

        val networkRequest = networkRequestProvider.getNetworkRequest(urlIngredient, NetworkRequest.Method.GET)
        val response = networkRequest.start()
        return if(response.succeeded() && response.code == 200) {
            parseIngredients(response) ?: LinkedList()
        } else {
            LinkedList()
        }
    }

    override suspend fun getAllForRecipeId(recipeId: Int) : LinkedList<IngredientRemote> {
        if (!authService.ensureSession()) return LinkedList()

        val networkRequest = networkRequestProvider.getNetworkRequest( "$urlRecipe/$recipeId/ingredient/", NetworkRequest.Method.GET)
        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            parseIngredients(response) ?: LinkedList()
        } else {
            LinkedList()
        }
    }

    override suspend fun get(id: Int) : IngredientRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest( "$urlIngredient/$id", NetworkRequest.Method.GET)
        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            val ingredients = parseIngredients(response) ?: return null
            return parseIngredient(ingredients)
        } else {
            null
        }
    }

    override suspend fun new(item: IngredientRemote) : IngredientRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlIngredient, NetworkRequest.Method.PUT)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }

        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            val ingredients = parseIngredients(response) ?: return null
            return parseIngredient(ingredients)
        } else {
            null
        }
    }

    override suspend fun update(item: IngredientRemote) : IngredientRemote? {
        if (!authService.ensureSession()) return null

        val networkRequest = networkRequestProvider.getNetworkRequest(urlIngredient, NetworkRequest.Method.POST)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }

        val response = networkRequest.start()

        return if (response.succeeded() && response.code == 200) {
            val ingredients = parseIngredients(response) ?: return null
            return parseIngredient(ingredients)
        } else {
            null
        }
    }

    override suspend fun delete(id: Int) : Boolean {
        if (!authService.ensureSession()) return false

        val networkRequest = networkRequestProvider.getNetworkRequest( "$urlIngredient/$id", NetworkRequest.Method.DELETE)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(emptyMap()))
                setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
            }

        val response = networkRequest.start()

        return response.succeeded() && response.code == 200
    }

    private fun parseIngredients(response: NetworkResponse) : Ingredients? {
        val responseString = response.responseAsString() ?: return null
        return Ingredients(responseString)
    }

    private fun parseIngredient(ingredients: Ingredients) : IngredientRemote? {
        return if (ingredients.isEmpty()) {
            null
        } else {
            ingredients[0]
        }
    }
}