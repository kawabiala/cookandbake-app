package com.pingwinek.jens.cookandbake.repos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.IngredientFB
import com.pingwinek.jens.cookandbake.sources.IngredientSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class IngredientRepository private constructor(val application: PingwinekCooksApplication) {

    private val ingredientSourceFB = application.getServiceLocator().getService(IngredientSourceFB::class.java)

    private val repoListData = MutableLiveData<LinkedList<IngredientFB>>()
    val ingredientListData = repoListData.map() {
        LinkedList<Ingredient>().apply {
            it.forEach { ingredientFB ->
                add(ingredientFB)
            }
        }
    }

    suspend fun getAll(recipeId: String) {
        updateIngredientList(ingredientSourceFB.getAllForRecipeId(recipeId))
    }

    suspend fun newIngredient(
        recipeId: String,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ) : IngredientFB? {
        val ingredientFB = ingredientSourceFB.new(IngredientFB(recipeId, quantity, quantityVerbal, unity, name))
        updateIngredientList(ingredientFB)
        return ingredientFB
    }

    suspend fun updateIngredient(
        ingredientId: String,
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

    suspend fun deleteIngredient(ingredientId: String) {
        val ingredient = repoListData.value?.find {
            it.id == ingredientId
        } ?: return
        deleteIngredient(ingredient)
    }

    suspend fun deleteIngredientForRecipeId(recipeId: String) {
        ingredientSourceFB.getAllForRecipeId(recipeId).forEach { ingredient ->
            deleteIngredient(ingredient)
        }
    }

    private suspend fun updateIngredient(ingredientFB: IngredientFB) {
        val updatedIngredient = ingredientSourceFB.update(ingredientFB)
        updateIngredientList(updatedIngredient)
    }

    private suspend fun deleteIngredient(ingredient: IngredientFB) : Boolean {
        val result = ingredientSourceFB.delete(ingredient)
        removeFromIngredientList(ingredient.id)
        return result
    }

    private fun updateIngredientList(updatedIngredientList: LinkedList<IngredientFB>) {
        val updatedList = repoListData.value ?: LinkedList<IngredientFB>()
        updatedList.apply {
            clear()
            updatedIngredientList.forEach { updatedIngredient ->
                add(updatedIngredient)
            }
            sortBy {
                val unity = it.unity ?: "zzz"
                "${unity}_${it.quantity}_${it.name}"
            }
        }
        repoListData.postValue(updatedList)
    }

    private fun updateIngredientList(ingredient: IngredientFB) {
        val updatedList = repoListData.value ?: LinkedList()
        updatedList.removeAll {
            it.id == ingredient.id
        }
        updatedList.add(ingredient)
        repoListData.postValue(updatedList)
    }

    private fun removeFromIngredientList(ingredientId: String) {
        val updatedList = repoListData.value ?: LinkedList()
        updatedList.removeAll {
            it.id == ingredientId
        }
        repoListData.postValue(updatedList)
    }

    companion object : SingletonHolder<IngredientRepository, PingwinekCooksApplication>(::IngredientRepository)

}