package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.Ingredient
import com.pingwinek.jens.cookandbake.IngredientRepository
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeRepository

class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    private val ingredientRepository = IngredientRepository.getInstance(application)
    private val recipeRepository = RecipeRepository.getInstance(application)

    val ingredientData = MutableLiveData<Ingredient>()
    val recipeData = MutableLiveData<Recipe>()

    fun newIngredient(recipeId: Int) {
        ingredientData.value = Ingredient(null, recipeId, null, null, "")
        loadRecipe(recipeId)
    }

    fun loadData(ingredientId: Int) {
        ingredientRepository.getIngredient(ingredientId) { ingredient ->
            ingredientData.postValue(ingredient)
        }
    }

    fun loadRecipe(recipeId: Int) {
        recipeRepository.getRecipe(recipeId) { recipe ->
            recipeData.postValue(recipe)
        }
    }

    fun save(name: String, quantity: Double?, unity: String?) {

        // input validation could be moved to something separate
        if (name.isEmpty()) {
            return
        }

        /*
        / if the viewmodel does not contain a recipe, we didn't get any from the server
         */
        ingredientData.value?.let {

            // create a new recipe, when id is null
            if (it.id == null) {
                val ingredient = Ingredient(null, it.recipeId, quantity, unity, name)
                ingredientRepository.putIngredient(ingredient) { ingredientFromResponse ->
                    ingredientData.postValue(ingredientFromResponse)
                }

            // otherwise update existing recipe
            } else {
                val ingredient = Ingredient(it.id, it.recipeId, quantity, unity, name)
                ingredientRepository.postIngredient(ingredient) { ingredientFromResponse ->
                    ingredientData.postValue(ingredientFromResponse)
                }
            }
        }
    }

}