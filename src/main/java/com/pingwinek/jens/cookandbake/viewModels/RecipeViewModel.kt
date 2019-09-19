package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.pingwinek.jens.cookandbake.Ingredient
import com.pingwinek.jens.cookandbake.IngredientRepository
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeRepository
import java.util.*

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)
    private val ingredientRepository = IngredientRepository.getInstance(application)

    var recipeId: Int? = null
    //var ingredientId: Int? = null

    val recipeData: LiveData<Recipe?> = Transformations.map(recipeRepository.recipeListData) { recipeList ->
        recipeId?.let {
            recipeList.find { recipe ->
                recipe.id == recipeId
            }
        }
    }
/*
    val ingredientData: LiveData<Ingredient?> = Transformations.map(ingredientRepository.ingredientListData) { ingredientList ->
        ingredientId?.let {
            ingredientList.find { ingredient ->
                ingredient.id == ingredientId
            }
        }
    }
*/
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
                val recipe = Recipe(it.id, title, description, instruction)
                recipeRepository.postRecipe(recipe)
            }
        } else {
            val recipe = Recipe(null, title, description, instruction)
            recipeRepository.putRecipe(recipe) { newRecipeId ->
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

        val ingredient = Ingredient(id, recipeId!!, quantity, unity, name)

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