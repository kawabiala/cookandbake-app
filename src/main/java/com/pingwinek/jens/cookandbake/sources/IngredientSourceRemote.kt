package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.sync.Promise
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

    override fun getAll() : Promise<LinkedList<IngredientRemote>> {
        val promise = Promise<LinkedList<IngredientRemote>>()

        authService.ensureSession { success ->
            if (success) {

                val networkRequest = networkRequestProvider.getNetworkRequest(
                    urlIngredient,
                    NetworkRequestProvider.Method.GET
                )
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

                networkResponseRouter.registerResponseRoute(
                    AbstractNetworkResponseRoutes.Result.SUCCESS,
                    200
                ) { _, _, response ->
                    promise.setResult(
                        Promise.Status.SUCCESS,
                        Ingredients(response)
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

    override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientRemote>> {
        val promise = Promise<LinkedList<IngredientRemote>>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest( "$urlRecipe/$recipeId/ingredient/", NetworkRequestProvider.Method.GET)
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

                networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
                    promise.setResult(
                        Promise.Status.SUCCESS,
                        Ingredients(response)
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

    override fun get(id: Int) : Promise<IngredientRemote> {
        val promise = Promise<IngredientRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest( "$urlIngredient/$id", NetworkRequestProvider.Method.GET)
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

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
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    override fun new(item: IngredientRemote) : Promise<IngredientRemote> {
        val promise = Promise<IngredientRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest(urlIngredient, NetworkRequestProvider.Method.PUT)
                networkRequest.setUploadDataProvider(
                    NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
                    NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
                )
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

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
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    override fun update(item: IngredientRemote) : Promise<IngredientRemote> {
        val promise = Promise<IngredientRemote>()

        authService.ensureSession { success ->
            if (success) {
                val networkRequest = networkRequestProvider.getNetworkRequest(urlIngredient, NetworkRequestProvider.Method.POST)
                networkRequest.setUploadDataProvider(
                    NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
                    NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
                )
                val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

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
                val networkRequest = networkRequestProvider.getNetworkRequest( "$urlIngredient/$id", NetworkRequestProvider.Method.DELETE)
                networkRequest.setUploadDataProvider(
                    NetworkRequestProvider.getUploadDataProvider(emptyMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
                    NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
                )
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