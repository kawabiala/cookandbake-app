package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import java.util.*

class IngredientSourceRemote private constructor(val application: Application) : IngredientSource<IngredientRemote> {

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)

    override fun getAll(callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest(INGREDIENTPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            callback(Source.Status.SUCCESS, Ingredients(response))
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
    }

    override fun getAllForRecipeId(recipeId: Int, callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$RECIPEPATH$recipeId/ingredient/", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            callback(Source.Status.SUCCESS, Ingredients(response))
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
    }

    override fun get(id: Int, callback: (Source.Status, IngredientRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$id", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(Source.Status.SUCCESS, ingredients[0])
            } else {
                callback(Source.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun new(item: IngredientRemote, callback: (Source.Status, IngredientRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(Source.Status.SUCCESS, ingredients[0])
            } else {
                callback(Source.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun update(item: IngredientRemote, callback: (Source.Status, IngredientRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(Source.Status.SUCCESS, ingredients[0])
            } else {
                callback(Source.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun delete(id: Int, callback: (Source.Status) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$id", NetworkRequestProvider.Method.DELETE)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(emptyMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, _, _ ->
            callback(Source.Status.SUCCESS)
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE)
        }
        networkRequest.start()
    }

    private fun retry(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) {
        AuthService.getInstance(application).onSessionInvalid { authCode, _ ->
            if (authCode == 200) {
                request.obtainNetworkResponseRouter().registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { _, _, _, _ ->
                    //Do nothing, especially don't loop
                }
                request.start()
            }
        }
    }

    companion object : SingletonHolder<IngredientSourceRemote, Application>(::IngredientSourceRemote)

}