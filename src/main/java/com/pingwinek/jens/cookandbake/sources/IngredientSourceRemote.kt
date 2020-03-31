package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.Ingredients
import java.util.*

/**
 * Source for retrieving and manipulating ingredients in a remote database
 *
 * @property application the application instance of the Android app
 */
class IngredientSourceRemote private constructor(val application: Application) : IngredientSource<IngredientRemote> {

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)

    override fun getAll() : Promise<LinkedList<IngredientRemote>> {
        val networkRequest = networkRequestProvider.getNetworkRequest(INGREDIENTPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<LinkedList<IngredientRemote>>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            promise.setResult(
                Promise.Status.SUCCESS,
                Ingredients(response)
            )
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
        return promise
    }

    override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientRemote>> {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$RECIPEPATH$recipeId/ingredient/", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<LinkedList<IngredientRemote>>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            promise.setResult(
                Promise.Status.SUCCESS,
                Ingredients(response)
            )
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
        return promise
    }

    override fun get(id: Int) : Promise<IngredientRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$id", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<IngredientRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun new(item: IngredientRemote) : Promise<IngredientRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<IngredientRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun update(item: IngredientRemote) : Promise<IngredientRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<IngredientRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$id", NetworkRequestProvider.Method.DELETE)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(emptyMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<Unit>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, _, _ ->
            promise.setResult(Promise.Status.SUCCESS, null)
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
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