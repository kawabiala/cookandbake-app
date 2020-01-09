package com.pingwinek.jens.cookandbake.repos

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.SyncManager
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.Source
import java.util.*

class IngredientRepository private constructor(val application: Application) {

    private val ingredientSourceLocal = IngredientSourceLocal.getInstance(application)
    private val syncManager = SyncManager.getInstance(application)

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
        syncManager.syncIngredients(recipeId) {
            fetchAll(recipeId)
        }
    }

    fun getIngredient(ingredientId: Int) {
        fetchIngredient(ingredientId)
        syncManager.syncIngredient(ingredientId) {
            fetchIngredient(ingredientId)
        }
    }

    fun new(
        recipeId: Int,
        quantity: Double?,
        unity: String?,
        name: String,
        confirmUpdate: (ingredientId: Int) -> Boolean
    ) {
        Log.i(this::class.java.name, "new")
        ingredientSourceLocal.new(IngredientLocal(recipeId, quantity, unity, name)) { status, ingredientLocal ->
            Log.i(this::class.java.name, "new: $status, $ingredientLocal")
            if (status == Source.Status.SUCCESS && ingredientLocal != null && confirmUpdate(ingredientLocal.id)) {
                Log.i(this::class.java.name, "new confirmed")
                updateIngredientList(ingredientLocal)
                syncManager.syncIngredient(ingredientLocal.id) {
                    fetchIngredient(ingredientLocal.id)
                }
            }
        }
    }

    fun update(
        ingredientId: Int,
        quantity: Double?,
        unity: String?,
        name: String
    ) {
        val ingredient = repoListData.value?.find {
            it.id == ingredientId
        } ?: return

        doUpdate(ingredient.getUpdated(quantity, unity, name))
    }

    fun deleteIngredient(ingredientId: Int, callback: () -> Unit) {
        val ingredient = repoListData.value?.find {
            it.id == ingredientId
        } ?: return

        doUpdate(ingredient.getDeleted())
    }

    private fun doUpdate(ingredientLocal: IngredientLocal) {
        ingredientSourceLocal.update(ingredientLocal) { status, updatedIngredient ->
            if (status == Source.Status.SUCCESS && updatedIngredient != null) {
                updateIngredientList(updatedIngredient)
                syncManager.syncIngredient(updatedIngredient.id) {
                    fetchIngredient(updatedIngredient.id)
                }
            }
        }
    }

    private fun fetchAll(recipeId: Int) {
        ingredientSourceLocal.getAllForRecipeId(recipeId) { _, ingredients ->
            repoListData.postValue(ingredients)
        }
    }

    private fun fetchIngredient(ingredientId: Int) {
        ingredientSourceLocal.get(ingredientId) { status, ingredient ->
            if (status == Source.Status.SUCCESS && ingredient != null) {
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

    private fun clearIngredientList() {
        repoListData.postValue(LinkedList())
    }

    private fun removeFromIngredientList(ingredientId: Int) {
        val ingredientList = repoListData.value
        ingredientList?.removeAll {
            it.id == ingredientId
        }
        repoListData.postValue(ingredientList)
    }

    companion object : SingletonHolder<IngredientRepository, Application>(::IngredientRepository)

}