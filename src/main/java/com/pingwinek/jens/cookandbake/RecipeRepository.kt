package com.pingwinek.jens.cookandbake

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)
    private val syncManager = SyncManager.getInstance(application)

    val recipeListData = MutableLiveData<LinkedList<RecipeLocal>>()

    fun getAll() {

        // Step 1: get local
        recipeSourceLocal.getAll { _, recipes ->
            recipeListData.postValue(recipes)
        }

        // Step 2: sync
        syncManager.syncRecipes {
            // Step 3: get local again
            recipeSourceLocal.getAll { _, recipes ->
                recipeListData.postValue(recipes)
            }
        }
    }

    fun getRecipe(recipeId: Int) {

        // Step 1: get local
        /*
        recipeSourceLocal.getRecipe(id) { recipe ->
            updateRecipeList(recipe)
        }

         */
        // syncManager.tbd

    }

    fun newRecipe(recipe: RecipeLocal, confirmUpdate: (recipeId: Int) -> Boolean) {
/*
        recipeSourceLocal.newRecipe(recipe) { newRecipe ->
            if (confirmUpdate(newRecipe.id)) {
                updateRecipeList(newRecipe)
            }
        }

 */
    }

    fun updateRecipe(recipe: RecipeLocal) {
        recipeSourceLocal.update(recipe) { _, updatedRecipe ->
            updatedRecipe?.let { updateRecipeList(it) }
        }
    }

    private fun updateRecipeList(updatedRecipe: RecipeLocal) {
        val recipeList = recipeListData.value ?: LinkedList()
        recipeList.removeAll {
            it.remoteId == updatedRecipe.remoteId
        }
        recipeList.add(updatedRecipe)
        recipeListData.postValue(recipeList)
    }

    companion object : SingletonHolder<RecipeRepository, Application>(::RecipeRepository)

}