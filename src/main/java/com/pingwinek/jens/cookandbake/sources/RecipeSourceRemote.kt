package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.RECIPEPATH
import com.pingwinek.jens.cookandbake.models.Recipes
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.RetryManager
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import java.util.*

/**
 * Source to retrieve and manipulate recipes from remote source
 *
 * @property NetworkRequestProvider instance of [NetworkRequestProvider]
 */
class RecipeSourceRemote private constructor(private val networkRequestProvider: NetworkRequestProvider) :
    RecipeSource<RecipeRemote> {

    var retryManager: RetryManager? = null

    override fun getAll() : Promise<LinkedList<RecipeRemote>> {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<LinkedList<RecipeRemote>>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            promise.setResult(
                Promise.Status.SUCCESS,
                Recipes(response)
            )
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
        return promise
    }

    override fun get(id: Int) : Promise<RecipeRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest("$RECIPEPATH$id", NetworkRequestProvider.Method.GET)

        val promise = Promise<RecipeRemote>()
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val recipes = Recipes(response)
            val recipe = if (recipes.isEmpty()) {
                null
            } else {
                recipes[0]
            }
            promise.setResult(Promise.Status.SUCCESS, recipe)
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun new(item: RecipeRemote) : Promise<RecipeRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<RecipeRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val recipes = Recipes(response)
                promise.setResult(Promise.Status.SUCCESS, recipes[0])
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun update(item: RecipeRemote) : Promise<RecipeRemote> {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        val promise = Promise<RecipeRemote>()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response, _ ->
            val recipes = Recipes(response)
                promise.setResult(Promise.Status.SUCCESS, recipes[0])
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        val networkRequest = networkRequestProvider.getNetworkRequest("$RECIPEPATH$id", NetworkRequestProvider.Method.DELETE)

        val promise = Promise<Unit>()
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, _, _ ->
            promise.setResult(Promise.Status.SUCCESS, null)
        }
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            promise.setResult(Promise.Status.FAILURE, null)
        }
        networkRequest.start()
        return promise
    }

    private fun retry(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) {
        retryManager?.retry(status, code, response, request)
    }

    companion object : SingletonHolder<RecipeSourceRemote, NetworkRequestProvider>(::RecipeSourceRemote)

}