package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.pingwinek.jens.cookandbake.activities.LOGOUT_EVENT
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    val tag = this::class.java.name

    fun getAll(callback: (recipeList: LinkedList<Recipe>) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
            callback(recipes)
        }
        networkResponseRouter.registerSuccessRoute(401) { response ->
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/recipe/",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getRecipe(recipeId: Int, callback: (recipe: Recipe) -> Unit ) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                callback(recipes[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) { response ->
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/recipe/" + recipeId,
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putRecipe(recipe: Recipe, callback: (recipe: Recipe) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "putRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                callback(recipes[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) { response ->
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/recipe/",
            NetworkRequest.Method.PUT,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            recipe.asMap(),
            networkResponseRouter)
    }

    fun postRecipe(recipe: Recipe, callback: (recipe: Recipe) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "postRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                callback(recipes[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) { response ->
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/recipe/",
            NetworkRequest.Method.POST,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            recipe.asMap(),
            networkResponseRouter)
    }

    companion object : SingletonHolder<RecipeRepository, Application>(::RecipeRepository)

}