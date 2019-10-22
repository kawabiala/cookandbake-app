package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    val recipeListData = MutableLiveData<LinkedList<Recipe>>()

    fun getAll() {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getRecipe response 200")
            recipeListData.postValue(Recipes(response))
        }
        networkRequest.start()
    }

    fun getRecipe(recipeId: Int) {
        val networkRequest = networkRequestProvider.getNetworkRequest("$RECIPEPATH$recipeId", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkRequest.start()
    }

    fun putRecipe(recipe: Recipe, confirmUpdate: (recipeId: Int) -> Boolean) {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(recipe.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
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
        networkRequest.start()
    }

    fun postRecipe(recipe: Recipe) {
        val networkRequest = networkRequestProvider.getNetworkRequest(RECIPEPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(recipe.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "postRecipe response 200")
            val recipes = Recipes(response)
            if (recipes.isNotEmpty()) {
                updateRecipeList(recipes[0])
            }
        }
        networkRequest.start()
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