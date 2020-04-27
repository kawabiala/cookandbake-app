package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.INGREDIENTPATH
import com.pingwinek.jens.cookandbake.RECIPEPATH
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.RetryManager
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.Ingredients
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

/**
 * Source for retrieving and manipulating ingredients in a remote database
 *
 * @property networkRequestProvider instance of [NetworkRequestProvider]
 */
class IngredientSourceRemote private constructor(private val networkRequestProvider: NetworkRequestProvider) : IngredientSource<IngredientRemote> {

    var retryManager: RetryManager? = null

    override fun getAll() : Promise<LinkedList<IngredientRemote>> {
        val networkRequest = networkRequestProvider.getNetworkRequest(INGREDIENTPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<LinkedList<IngredientRemote>>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            promise.setResult(
                Promise.Status.SUCCESS,
                Ingredients(response)
            )
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, LinkedList())
        }

        val retryRequest = networkRequest.clone()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { status, code, response ->
            retryManager?.retry(status, code, response, retryRequest)
        }
        networkRequest.start()
        return promise
    }

    override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientRemote>> {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$RECIPEPATH$recipeId/ingredient/", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<LinkedList<IngredientRemote>>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            promise.setResult(
                Promise.Status.SUCCESS,
                Ingredients(response)
            )
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, LinkedList())
        }

        val retryRequest = networkRequest.clone()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { status, code, response ->
            retryManager?.retry(status, code, response, retryRequest)
        }
        networkRequest.start()
        return promise
    }

    override fun get(id: Int) : Promise<IngredientRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$id", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<IngredientRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }

        val retryRequest = networkRequest.clone()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { status, code, response ->
            retryManager?.retry(status, code, response, retryRequest)
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
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
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
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            val ingredients =
                Ingredients(response)
            if (ingredients.isNotEmpty()) {
                promise.setResult(Promise.Status.SUCCESS, ingredients[0])
            } else {
                promise.setResult(Promise.Status.SUCCESS, null)
            }
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
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
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, _ ->
            promise.setResult(Promise.Status.SUCCESS, null)
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    companion object : SingletonHolder<IngredientSourceRemote, NetworkRequestProvider   >(::IngredientSourceRemote)

}