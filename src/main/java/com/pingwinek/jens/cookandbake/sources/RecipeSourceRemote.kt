package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.RECIPEPATH
import com.pingwinek.jens.cookandbake.Recipes
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import java.util.*

class RecipeSourceRemote private constructor(val application: Application) :
    RecipeSource<RecipeRemote> {

    private val tag: String = this::class.java.name

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)

    override fun getAll(callback: (Source.Status, LinkedList<RecipeRemote>) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getRecipe response 200")
            callback(Source.Status.SUCCESS, Recipes(response))
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, LinkedList())
        }
        networkRequest.start()
    }

    override fun get(id: Int, callback: (Source.Status, RecipeRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest("$RECIPEPATH$id", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
                callback(Source.Status.SUCCESS, recipes[0])
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun new(item: RecipeRemote, callback: (Source.Status, RecipeRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "newRecipe response 200")
            val recipes = Recipes(response)
                callback(Source.Status.SUCCESS, recipes[0])
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun update(item: RecipeRemote, callback: (Source.Status, RecipeRemote?) -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(item.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "updateRecipe response 200")
            val recipes = Recipes(response)
                callback(Source.Status.SUCCESS, recipes[0])
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkResponseRouter.registerDefaultResponseRoute { _, _, _, _ ->
            callback(Source.Status.FAILURE, null)
        }
        networkRequest.start()
    }

    override fun delete(id: Int, callback: (Source.Status) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retry(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) {
        Log.i(tag, "getRecipe response 401")
        AuthService.getInstance(application).onSessionInvalid { authCode, authResponse ->
            if (authCode == 200) {
                request.obtainNetworkResponseRouter().registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { _, _, _, _ ->
                    //Do nothing, especially don't loop
                }
                request.start()
            }
        }
    }

    companion object : SingletonHolder<RecipeSourceRemote, Application>(::RecipeSourceRemote)

}