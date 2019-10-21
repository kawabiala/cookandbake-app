package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    val networkRequest = NetworkRequestProvider.getInstance(application)
    val ingredientListData = MutableLiveData<LinkedList<Ingredient>>()
    val networkError = MutableLiveData<String>()

    fun getAll(recipeId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "getIngredient response 200")
            ingredientListData.postValue(Ingredients(response))
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearIngredientList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            "$RECIPEPATH$recipeId/ingredient/",
            NetworkRequestProvider.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getIngredient(ingredientId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "getIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearIngredientList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            "$INGREDIENTPATH$ingredientId",
            NetworkRequestProvider.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putIngredient(ingredient: Ingredient, confirmUpdate: (ingredientId: Int) -> Boolean) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
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
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearIngredientList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            INGREDIENTPATH,
            NetworkRequestProvider.Method.PUT,
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun postIngredient(ingredient: Ingredient) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "postIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearIngredientList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            INGREDIENTPATH,
            NetworkRequestProvider.Method.POST,
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED,
            ingredient.asMap(),
            networkResponseRouter)
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "deleteIngredient response 200")
            callback()
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearIngredientList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            "$INGREDIENTPATH$ingredientId",
            NetworkRequestProvider.Method.DELETE,
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED,
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

    private fun clearIngredientList() {
        ingredientListData.postValue(LinkedList())
    }

    companion object : SingletonHolder<IngredientRepository, Application>(::IngredientRepository)

}