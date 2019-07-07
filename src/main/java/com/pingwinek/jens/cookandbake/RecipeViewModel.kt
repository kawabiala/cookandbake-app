package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)

    val recipeData = MutableLiveData<Recipe>()

    val isEditableTitle = MutableLiveData<Boolean>()

    init {
        isEditableTitle.value = false
        recipeData.value = Recipe(null, "", null)
    }

    fun loadData(recipeId: Int) {
        recipeRepository.getRecipe(recipeId) { recipe ->
            recipeData.postValue(recipe)
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

}