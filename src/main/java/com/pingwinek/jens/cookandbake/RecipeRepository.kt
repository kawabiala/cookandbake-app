package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    val networkRequest = NetworkRequestProvider.getInstance(application)
    val recipeListData = MutableLiveData<LinkedList<Recipe>>()
    val networkError = MutableLiveData<String>()

    fun getAll() {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "getRecipe response 200")
            recipeListData.postValue(Recipes(response))
         }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            Log.i(tag, "getRecipe response $response")
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                Log.i(tag, "auth: $code : $response")
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearRecipeList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequestProvider.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getRecipe(recipeId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            Log.i(tag, "getRecipe response $response")
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearRecipeList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            "$RECIPEPATH$recipeId",
            NetworkRequestProvider.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putRecipe(recipe: Recipe, confirmUpdate: (recipeId: Int) -> Boolean) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "putRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                val newRecipe = recipes[0]
                newRecipe.id?.let {
                    if (confirmUpdate(it)) {
                        updateRecipeList(newRecipe)
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
                        clearRecipeList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequestProvider.Method.PUT,
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED,
            recipe.asMap(),
            networkResponseRouter)
    }

    fun postRecipe(recipe: Recipe) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(tag, "postRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,401) { status, code, response ->
            AuthService.getInstance(application).onSessionInvalid { code, response ->
                when (code) {
                    200 -> {}
                    else -> {
                        networkError.postValue(response)
                        clearRecipeList()
                    }
                }
            }
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequestProvider.Method.POST,
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED,
            recipe.asMap(),
            networkResponseRouter)
    }

    private fun updateRecipeList(updatedRecipe: Recipe) {
        val recipeList = recipeListData.value ?: LinkedList()
        recipeList.removeAll {
            it.id == updatedRecipe.id
        }
        recipeList.add(updatedRecipe)
        recipeListData.postValue(recipeList)
    }

    private fun clearRecipeList() {
        recipeListData.postValue(LinkedList())
    }

    companion object : SingletonHolder<RecipeRepository, Application>(::RecipeRepository)

}