package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.IngredientRepository
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.RecipeRepository
import java.util.*

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)
    private val ingredientRepository = IngredientRepository.getInstance(application)

    var recipeId: Int? = null

    val recipeData: LiveData<RecipeLocal?> = Transformations.map(recipeRepository.recipeListData) { recipeList ->
        recipeId?.let {
            recipeList.find { recipe ->
                recipe.remoteId == recipeId
            }
        }
    }

    val ingredientListData = ingredientRepository.ingredientListData

    init {
        ingredientListData.value = LinkedList()
    }

    fun loadData() {
        recipeId?.let { id ->
            recipeRepository.getRecipe(id)
            ingredientRepository.getAll(id)
        }
    }

    fun save(title: String, description: String, instruction: String) {

        // input validation could be moved to something separate
        if (title.isEmpty()) {
            return
        }

        if (recipeId != null) {
            recipeData.value?.let {
                val recipe = RecipeLocal(
                    it.rowid,
                    it.remoteId,
                    title,
                    description,
                    instruction
                )
                recipeRepository.updateRecipe(recipe)
            }
        } else {
            val recipe = RecipeLocal(
                0,
                null,
                title,
                description,
                instruction
            )
            recipeRepository.newRecipe(recipe) { newRecipeId ->
                recipeId = newRecipeId
                true
            }
        }
    }

    fun saveIngredient(id: Int?, name: String, quantity: Double?, unity: String?) {
        if (name.isEmpty()) {
            return
        }

        if (recipeId == null){
            return
        }

        val ingredient =
            IngredientRemote(id, recipeId!!, quantity, unity, name)

        if (id == null) {
            ingredientRepository.putIngredient(ingredient) {
                true
            }
        } else {
            ingredientRepository.postIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredientId: Int) {
        ingredientRepository.deleteIngredient(ingredientId) {
            loadData()
        }
    }

}