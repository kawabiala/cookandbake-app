package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.sync.Promise
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

    override fun getAll() : Promise<LinkedList<RecipeRemote>> {
        val promise = Promise<LinkedList<RecipeRemote>>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.GET)
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
                    promise.setResult(
                        Promise.Status.SUCCESS,
                        Recipes(response)
                    )
                }
                networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
                    promise.setResult(Promise.Status.FAILURE, LinkedList())
                }

                networkRequest.start()
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }

        return promise
    }

    override fun get(id: Int) : Promise<RecipeRemote> {
        val promise = Promise<RecipeRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest("$urlRecipe/$id", NetworkRequest.Method.GET)

                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()
                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
                    val recipes = Recipes(response)
                    val recipe = if (recipes.isEmpty()) {
                        null
                    } else {
                        recipes[0]
                    }
                    promise.setResult(Promise.Status.SUCCESS, recipe)
                }
                networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
                    promise.setResult(Promise.Status.FAILURE, null)
                }

                networkRequest.start()
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    override fun new(item: RecipeRemote) : Promise<RecipeRemote> {
        val promise = Promise<RecipeRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.PUT)
                    .apply {
                        setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                        setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
                    }

                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
                    val recipes = Recipes(response)
                    promise.setResult(Promise.Status.SUCCESS, recipes[0])
                }
                networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
                    promise.setResult(Promise.Status.FAILURE, null)
                }
                networkRequest.start()
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    override fun update(item: RecipeRemote) : Promise<RecipeRemote> {
        val promise = Promise<RecipeRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest(urlRecipe, NetworkRequest.Method.POST)
                    .apply {
                        setOutputString(NetworkRequestProvider.toBody(item.asMap()))
                        setContentType(NetworkRequest.ContentType.APPLICATION_URLENCODED)
                    }

                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
                    val recipes = Recipes(response)
                    val recipe = if (recipes.isEmpty()) {
                        null
                    } else {
                        recipes[0]
                    }
                    promise.setResult(Promise.Status.SUCCESS, recipe)
                }
                networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
                    promise.setResult(Promise.Status.FAILURE, null)
                }
                networkRequest.start()
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        val promise = Promise<Unit>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest("$urlRecipe/$id", NetworkRequest.Method.DELETE)

                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()
                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, _ ->
                    promise.setResult(Promise.Status.SUCCESS, null)
                }
                networkResponseRouter.registerDefaultResponseRoute { _, _, _ ->
                    promise.setResult(Promise.Status.FAILURE, null)
                }
                networkRequest.start()
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }
}