package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class IngredientRepository private constructor(val application: PingwinekCooksApplication) {

    private val ingredientSourceLocal = application.getServiceLocator().getService(IngredientSourceLocal::class.java)
    private val syncService = application.getServiceLocator().getService(SyncService::class.java)

    private val repoListData = MutableLiveData<LinkedList<IngredientLocal>>()
    val ingredientListData = Transformations.map(repoListData) {
        LinkedList<Ingredient>().apply {
            it.forEach { ingredientLocal ->
                if (!ingredientLocal.flagAsDeleted) {
                    add(ingredientLocal)
                }
            }
        }
    }

    fun getAll(recipeId: Int) {
        fetchAll(recipeId)
        syncIngredients(recipeId) {
            fetchAll(recipeId)
        }
    }

    @Suppress("Unused")
    fun getIngredient(ingredientId: Int) {
        fetchIngredient(ingredientId)
        syncIngredient(ingredientId) {
            fetchIngredient(ingredientId)
        }
    }

    fun new(
        recipeId: Int,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String,
        confirmUpdate: (ingredientId: Int) -> Boolean
    ) {
        ingredientSourceLocal.new(IngredientLocal(recipeId, quantity, quantityVerbal, unity, name))
            .setResultHandler{ result ->
                val status = result.status
                val ingredientLocal = result.value
                if (status == Promise.Status.SUCCESS && ingredientLocal != null && confirmUpdate(ingredientLocal.id)) {
                    Log.i(this::class.java.name, "new confirmed")
                    updateIngredientList(ingredientLocal)
                    syncIngredient(ingredientLocal.id) {
                        fetchIngredient(ingredientLocal.id)
                    }
                }
        }
    }

    fun update(
        ingredientId: Int,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ) {
        val ingredient = repoListData.value?.find {
            it.id == ingredientId
        } ?: return

        doUpdate(ingredient.getUpdated(quantity, quantityVerbal, unity, name))
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        ingredientSourceLocal.flagAsDeleted(ingredientId)
            .setResultHandler{ result ->
                val status = result.status
                val ingredient = result.value
                if (status == Promise.Status.SUCCESS && ingredient != null) {
                    updateIngredientList(ingredient)
                    syncIngredient(ingredient.id) {
                        fetchIngredient(ingredient.id)
                        callback()
                    }
                } else {
                    callback()
                }
        }
    }

    fun deleteIngredientForRecipeId(recipeId: Int, callback: () -> Unit) {
        ingredientSourceLocal.getAllForRecipeId(recipeId)
            .setResultHandler{ result ->
                val status = result.status
                val ingredients = result.value
                if (status == Promise.Status.SUCCESS && ingredients != null) {
                    ingredients.forEach { ingredient ->
                        deleteIngredient(ingredient.id) {}
                    }
                }
                callback()
        }
    }

    private fun doUpdate(ingredientLocal: IngredientLocal) {
        ingredientSourceLocal.update(ingredientLocal)
            .setResultHandler{ result ->
                val status = result.status
                val updatedIngredient = result.value
                if (status == Promise.Status.SUCCESS && updatedIngredient != null) {
                    updateIngredientList(updatedIngredient)
                    syncIngredient(updatedIngredient.id) {
                        fetchIngredient(updatedIngredient.id)
                    }
                }
        }
    }

    private fun fetchAll(recipeId: Int) {
        ingredientSourceLocal.getAllForRecipeId(recipeId)
            .setResultHandler{ result ->
                val ingredients = result.value
                repoListData.postValue(ingredients)
        }
    }

    private fun fetchIngredient(ingredientId: Int) {
        ingredientSourceLocal.get(ingredientId)
            .setResultHandler{ result ->
                val status = result.status
                val ingredient = result.value
                if (status == Promise.Status.SUCCESS && ingredient != null) {
                    updateIngredientList(ingredient)
                } else {
                    removeFromIngredientList(ingredientId)
                }
        }
    }

    private fun updateIngredientList(ingredient: IngredientLocal) {
        val ingredientList = repoListData.value ?: LinkedList()
        ingredientList.removeAll {
            it.id == ingredient.id
        }
        ingredientList.add(ingredient)
        repoListData.postValue(ingredientList)
    }

    private fun removeFromIngredientList(ingredientId: Int) {
        val ingredientList = repoListData.value
        ingredientList?.removeAll {
            it.id == ingredientId
        }
        repoListData.postValue(ingredientList)
    }

    private fun syncIngredient(ingredientId: Int, callback: () -> Unit) {
        syncService.syncEntry<IngredientLocal, IngredientRemote>(ingredientId, callback)
    }

    private fun syncIngredients(recipeId: Int, callback: () -> Unit) {
        syncService.syncByParentId<IngredientLocal, IngredientRemote>(recipeId, callback)
    }

    @Suppress("Unused")
    private fun syncIngredients(callback: () -> Unit) {
        syncService.sync<IngredientLocal, IngredientRemote>(callback)
    }

    companion object : SingletonHolder<IngredientRepository, PingwinekCooksApplication>(::IngredientRepository)

}