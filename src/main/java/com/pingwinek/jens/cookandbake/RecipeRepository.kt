package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    val networkRequest = NetworkRequest.getInstance(application)
    val recipeListData = MutableLiveData<LinkedList<Recipe>>()

    fun getAll() {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getRecipe response 200")
            recipeListData.postValue(Recipes(response))
         }
        networkResponseRouter.registerSuccessRoute(401) {
            Log.i(tag, "getRecipe response $it")
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun getRecipe(recipeId: Int) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            Log.i(tag, "getRecipe response $it")
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            "$RECIPEPATH$recipeId",
            NetworkRequest.Method.GET,
            null,
            mapOf(),
            networkResponseRouter)
    }

    fun putRecipe(recipe: Recipe, confirmUpdate: (recipeId: Int) -> Boolean) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
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
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequest.Method.PUT,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
            recipe.asMap(),
            networkResponseRouter)
    }

    fun postRecipe(recipe: Recipe) {
        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()

        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(tag, "postRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkResponseRouter.registerSuccessRoute(401) {
            AuthService.getInstance(application).onSessionInvalid()
        }
        networkRequest.runRequest(
            RECIPEPATH,
            NetworkRequest.Method.POST,
            NetworkRequest.ContentType.APPLICATION_URLENCODED,
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

    companion object : SingletonHolder<RecipeRepository, Application>(::RecipeRepository)

}