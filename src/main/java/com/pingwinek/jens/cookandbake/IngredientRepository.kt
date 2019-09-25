package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    val networkRequest = NetworkRequest.getInstance(application)
    val ingredientListData = MutableLiveData<LinkedList<Ingredient>>()

    fun getAll(recipeId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getIngredient response 200")
            ingredientListData.postValue(Ingredients(response))
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            "$RECIPEPATH$recipeId/ingredient/",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getIngredient(ingredientId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            "$INGREDIENTPATH$ingredientId",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putIngredient(ingredient: Ingredient, confirmUpdate: (ingredientId: Int) -> Boolean) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "putIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                val newIngredient = ingredients[0]
                newIngredient.id?.let {
                    if (confirmUpdate(it)) {
                        updateIngredientList(newIngredient)
                    }
                }
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            INGREDIENTPATH,
            NetworkRequest.Method.PUT,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun postIngredient(ingredient: Ingredient) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "postIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            INGREDIENTPATH,
            NetworkRequest.Method.POST,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) {
            Log.i(tag, "deleteIngredient response 200")
            callback()
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            "$INGREDIENTPATH$ingredientId",
            NetworkRequest.Method.DELETE,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            emptyMap(),
            networkResponseRouter)
    }

    private fun updateIngredientList(ingredient: Ingredient) {
        val ingredientList = ingredientListData.value ?: LinkedList()
        ingredientList.removeAll {
            it.id == ingredient.id
        }
        ingredientList.add(ingredient)
        ingredientListData.postValue(ingredientList)
    }

    companion object : SingletonHolder<IngredientRepository, Application>(::IngredientRepository)

}