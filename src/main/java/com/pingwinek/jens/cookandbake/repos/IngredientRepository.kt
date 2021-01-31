package com.pingwinek.jens.cookandbake.repos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
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

    suspend fun getAll(recipeId: Int) {
        updateIngredientList(ingredientSourceLocal.getAllForRecipeId(recipeId))
        syncIngredients(recipeId)
        updateIngredientList(ingredientSourceLocal.getAllForRecipeId(recipeId))
    }

    @Suppress("Unused")
    suspend fun getIngredient(ingredientId: Int) {
        fetchIngredient(ingredientId)
        syncIngredient(ingredientId)
        fetchIngredient(ingredientId)
    }

    suspend fun newIngredient(
        recipeId: Int,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String,
        confirmUpdate: (ingredientId: Int) -> Boolean
    ) {
        val ingredientLocal = ingredientSourceLocal.new(IngredientLocal(recipeId, quantity, quantityVerbal, unity, name))
        if (confirmUpdate(ingredientLocal.id)) {
            updateIngredientList(ingredientLocal)
            syncIngredient(ingredientLocal.id)
            fetchIngredient(ingredientLocal.id)
        }
    }

    suspend fun updateIngredient(
        ingredientId: Int,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ) {
        val ingredient = repoListData.value?.find {
            it.id == ingredientId
        } ?: return

        updateIngredient(ingredient.getUpdated(quantity, quantityVerbal, unity, name))
    }

    suspend fun deleteIngredient(ingredientId: Int) {
        val ingredient = ingredientSourceLocal.flagAsDeleted(ingredientId) ?: return
        updateIngredientList(ingredient)
        syncIngredient(ingredient.id)
        fetchIngredient(ingredient.id)
    }

    suspend fun deleteIngredientForRecipeId(recipeId: Int) {
        ingredientSourceLocal.getAllForRecipeId(recipeId).forEach { ingredient ->
            deleteIngredient(ingredient.id)
        }
    }

    private suspend fun updateIngredient(ingredientLocal: IngredientLocal) {
        val updatedIngredient = ingredientSourceLocal.update(ingredientLocal) ?: return
        updateIngredientList(updatedIngredient)
        syncIngredient(updatedIngredient.id)
        fetchIngredient(updatedIngredient.id)
    }

    private suspend fun fetchIngredient(ingredientId: Int) {
        val ingredient = ingredientSourceLocal.get(ingredientId)
        if (ingredient != null) {
            updateIngredientList(ingredient)
        } else {
            removeFromIngredientList(ingredientId)
        }
    }

    private fun updateIngredientList(updatedIngredientList: LinkedList<IngredientLocal>) {
        val updatedList = repoListData.value ?: LinkedList<IngredientLocal>()
        updatedList.apply {
            clear()
            updatedIngredientList.forEach { updatedIngredient ->
                if (!updatedIngredient.flagAsDeleted) add(updatedIngredient)
            }
            sortBy {
                val unity = it.unity ?: "zzz"
                "${unity}_${it.quantity}_${it.name}"
            }
        }
        repoListData.postValue(updatedList)
    }

    private fun updateIngredientList(ingredient: IngredientLocal) {
        val updatedList = repoListData.value ?: LinkedList()
        updatedList.removeAll {
            it.id == ingredient.id
        }
        updatedList.add(ingredient)
        repoListData.postValue(updatedList)
    }

    private fun removeFromIngredientList(ingredientId: Int) {
        val updatedList = repoListData.value
        updatedList?.removeAll {
            it.id == ingredientId
        }
        repoListData.postValue(updatedList)
    }

    private suspend fun syncIngredient(ingredientId: Int) {
        syncService.syncEntry<IngredientLocal, IngredientRemote>(ingredientId)
    }

    private suspend fun syncIngredients(recipeId: Int) {
        syncService.syncByParentId<IngredientLocal, IngredientRemote>(recipeId)
    }

    @Suppress("Unused")
    private suspend fun syncIngredients() {
        syncService.sync<IngredientLocal, IngredientRemote>()
    }

    companion object : SingletonHolder<IngredientRepository, PingwinekCooksApplication>(::IngredientRepository)

}