package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    val ingredientListData = MutableLiveData<LinkedList<Ingredient>>()

    fun getAll(recipeId: Int) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$RECIPEPATH$recipeId/ingredient/", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getIngredient response 200")
            ingredientListData.postValue(Ingredients(response))
        }
        networkRequest.start()
    }

    fun getIngredient(ingredientId: Int) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$ingredientId", NetworkRequestProvider.Method.GET)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "getIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkRequest.start()
    }

    fun putIngredient(ingredient: Ingredient, confirmUpdate: (ingredientId: Int) -> Boolean) {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(ingredient.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
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
        networkRequest.start()
    }

    fun postIngredient(ingredient: Ingredient) {
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(ingredient.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "postIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkRequest.start()
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$ingredientId", NetworkRequestProvider.Method.DELETE)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(emptyMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "deleteIngredient response 200")
            callback()
        }
        networkRequest.start()
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