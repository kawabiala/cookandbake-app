package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeRepository

class InstructionViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)

    val recipeData = MutableLiveData<Recipe>()

    fun load(recipeId: Int) {
        recipeRepository.getRecipe(recipeId) { recipe ->
            recipeData.postValue(recipe)
        }
    }

    fun save(instruction: String) {
        val oldRecipe = recipeData.value
        oldRecipe?.let { o ->
            val newRecipe = Recipe(o.id, o.title, o.description, instruction)
            recipeRepository.postRecipe(newRecipe) {
                recipeData.postValue(it)
            }
        }
    }
}
