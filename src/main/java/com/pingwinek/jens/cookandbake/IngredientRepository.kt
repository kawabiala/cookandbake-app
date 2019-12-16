package com.pingwinek.jens.cookandbake

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.Source
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val tag: String = this::class.java.name

    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val ingredientSourceLocal = IngredientSourceLocal.getInstance(application)
    private val syncManager = SyncManager.getInstance(application)

    val ingredientListData = MutableLiveData<LinkedList<IngredientLocal>>()

    fun getAll(recipeId: Int) {
        ingredientSourceLocal.getAllForRecipeId(recipeId) { _, ingredients ->
            ingredientListData.postValue(ingredients)
        }
        syncManager.syncIngredients(recipeId) {
            ingredientSourceLocal.getAllForRecipeId(recipeId) { _, _ ->}
        }
    }

    fun getIngredient(ingredientId: Int) {
        ingredientSourceLocal.get(ingredientId) { status, ingredient ->
            if (status == Source.Status.SUCCESS && ingredient != null) {
                updateIngredientList(ingredient)
            } else {
                // delete from ingredientList
            }
        }
    }

    fun putIngredient(ingredient: IngredientRemote, confirmUpdate: (ingredientId: Int) -> Boolean) {
        /*
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.PUT)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(ingredient.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "newIngredient response 200")
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
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkRequest.start()

         */
    }

    fun postIngredient(ingredient: IngredientRemote) {
        /*
        val networkRequest = networkRequestProvider.getNetworkRequest( INGREDIENTPATH, NetworkRequestProvider.Method.POST)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(ingredient.asMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "updateIngredient response 200")
            val ingredients = Ingredients(response)
            if (ingredients.isNotEmpty()) {
                updateIngredientList(ingredients[0])
            }
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkRequest.start()

         */
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        /*
        val networkRequest = networkRequestProvider.getNetworkRequest( "$INGREDIENTPATH$ingredientId", NetworkRequestProvider.Method.DELETE)
        networkRequest.setUploadDataProvider(
            NetworkRequestProvider.getUploadDataProvider(emptyMap(), NetworkRequestProvider.ContentType.APPLICATION_URLENCODED),
            NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
        )
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(tag, "deleteIngredient response 200")
            callback()
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401, this::retry)
        networkRequest.start()

         */
    }

    private fun updateIngredientList(ingredient: IngredientLocal) {
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

    private fun retry(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) {
        Log.i(tag, "getRecipe response 401")
        AuthService.getInstance(application).onSessionInvalid() { authCode, authResponse ->
            if (authCode == 200) {
                request.obtainNetworkResponseRouter().registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { _, _, _, _ ->
                    //Do nothing, especially don't loop
                }
                request.start()
            } else {
                clearIngredientList()
            }
        }
    }

    companion object : SingletonHolder<IngredientRepository, Application>(::IngredientRepository)

}