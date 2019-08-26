package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import java.util.*

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)
    private val ingredientRepository = IngredientRepository.getInstance(application)

    val recipeData = MutableLiveData<Recipe>()
    val ingredientListData = MutableLiveData<LinkedList<Ingredient>>()

    val isEditableTitle = MutableLiveData<Boolean>()

    init {
        isEditableTitle.value = false
        recipeData.value = Recipe(null, "", null)
        ingredientListData.value = LinkedList()
    }

    fun loadData(recipeId: Int) {
        recipeRepository.getRecipe(recipeId) { recipe ->
            recipeData.postValue(recipe)
        }
        ingredientRepository.getAll(recipeId) { ingredientList ->
            ingredientListData.postValue(ingredientList)
        }
    }

    fun save(title: String, description: String) {

        // input validation could be moved to something separate
        if (title == null || title.length == 0) {
            return
        }

        var recipe = recipeData.value

        /*
        / if the viewmodel does not contain a recipe, we didn't get any from the server
         */
        recipe?.let {

            // create a new recipe, when id is null
            if (it.id == null) {
                val _recipe = Recipe(null, title, description)
                recipeRepository.putRecipe(_recipe) { recipeFromResponse ->
                    recipeData.postValue(recipeFromResponse)
                }

            // otherwise update existing recipe
            } else {
                val _recipe = Recipe(it.id, title, description)
                recipeRepository.postRecipe(_recipe) { recipeFromResponse ->
                    recipeData.postValue(recipeFromResponse)
                }
            }
        }
    }

    fun deleteIngredient(ingredientId: Int) {
        ingredientRepository.deleteIngredient(ingredientId) {
            Log.e(this::class.java.name, "deleted")
            val recipeId = recipeData.value?.id
            Log.e(this::class.java.name, "load for $recipeId")
            recipeId?.let {
                Log.e(this::class.java.name, "loadData")
                loadData(it)
            }
        }
        Log.e(this::class.java.name, "Deleting $ingredientId")
    }

}