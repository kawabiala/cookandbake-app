package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.pingwinek.jens.cookandbake.activities.LOGOUT_EVENT
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    fun getAll(recipeId: Int, callback: (ingredientList: LinkedList<Ingredient>) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getIngredient response 200")
            val ingredients = Ingredients(response)
            callback(ingredients)
        }
        networkResponseRouter.registerSuccessRoute(401) {
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/recipe/$recipeId/ingredient/",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getIngredient(ingredientId: Int, callback: (ingredient: Ingredient) -> Unit ) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(ingredients[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/ingredient/$ingredientId",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putIngredient(ingredient: Ingredient, callback: (ingredient: Ingredient) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "putIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(ingredients[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/ingredient/",
            NetworkRequest.Method.PUT,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun postIngredient(ingredient: Ingredient, callback: (ingredient: Ingredient) -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "postIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                callback(ingredients[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/ingredient/",
            NetworkRequest.Method.POST,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        val networkResponseRouter = NetworkResponseRouter()
        val networkRequest = NetworkRequest.getInstance(application)

        networkResponseRouter.registerSuccessRoute(200) {
            Log.i(tag, "deleteIngredient response 200")
            callback()
        }
        networkResponseRouter.registerSuccessRoute(401) {
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }
        networkRequest.runRequest(
            "https://www.pingwinek.de/cookandbake/api/ingredient/$ingredientId",
            NetworkRequest.Method.DELETE,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            emptyMap(),
            networkResponseRouter)
    }

    companion object : SingletonHolder<IngredientRepository, Application>(::IngredientRepository)

}