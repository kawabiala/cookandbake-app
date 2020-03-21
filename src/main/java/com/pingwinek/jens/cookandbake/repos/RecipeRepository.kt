package com.pingwinek.jens.cookandbake.repos

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.sync.SyncManager
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import java.util.*

class RecipeRepository private constructor(val application: Application) {

    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)
    private val syncManager = SyncManager.getInstance(application)
    private val syncService = SyncService.getInstance(application)

    private val repoListData = MutableLiveData<LinkedList<RecipeLocal>>()
    val recipeListData: LiveData<LinkedList<Recipe>> = Transformations.map(repoListData) {
        LinkedList<Recipe>().apply {
            it.forEach { recipeLocal ->
                if (!recipeLocal.flagAsDeleted) {
                    add(recipeLocal)
                }
            }
        }
    }

    fun getAll() {
        fetchAll()
        syncManager.syncRecipes {
            fetchAll()
        }
        syncService.sync<RecipeLocal, RecipeRemote> {
            fetchAll()
        }
    }

    fun getRecipe(recipeId: Int) {
        fetchRecipe(recipeId)
        syncManager.syncRecipe(recipeId) {
            fetchRecipe(recipeId)
        }
    }

    fun newRecipe(
        title: String,
        description: String?,
        instruction: String?,
        confirmUpdate: (recipeId: Int) -> Boolean
    ) {
        recipeSourceLocal.new(RecipeLocal(title, description, instruction)) { status, newRecipe ->
            if (status == Source.Status.SUCCESS && newRecipe != null && confirmUpdate(newRecipe.id)) {
                updateRecipeList(newRecipe)
                syncManager.syncRecipe(newRecipe.id) {
                    fetchRecipe(newRecipe.id)
                }
            }
        }
    }

    fun updateRecipe(
        id: Int,
        title: String,
        description: String?,
        instruction: String?
    ) {
        val recipe = repoListData.value?.find {
            it.id == id
        } ?: return

        recipeSourceLocal.update(recipe.getUpdated(title, description, instruction)) { status, updatedRecipe ->
            if (status == Source.Status.SUCCESS && updatedRecipe != null) {
                updateRecipeList(updatedRecipe)
                syncManager.syncRecipe(updatedRecipe.id) {
                    fetchRecipe(updatedRecipe.id)
                }
            }
        }
    }

    fun deleteRecipe(recipeId: Int, callback: () -> Unit) {
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId) {
            recipeSourceLocal.flagAsDeleted(recipeId) { status, recipe ->
                System.out.println("Flag recipe $recipe")
                if (status == Source.Status.SUCCESS && recipe != null) {
                    updateRecipeList(recipe)
                    syncManager.syncRecipe(recipe.id) {
                        fetchRecipe(recipe.id)
                    }
                }
            }
        }
    }

    private fun fetchAll() {
        recipeSourceLocal.getAll { _, recipes ->
            repoListData.postValue(recipes)
        }
    }

    private fun fetchRecipe(recipeId: Int) {
        recipeSourceLocal.get(recipeId) { status, recipe ->
            if (status == Source.Status.SUCCESS && recipe != null) {
                updateRecipeList(recipe)
            } else {
                removeFromRecipeList(recipeId)
            }
        }
    }

    private fun updateRecipeList(updatedRecipe: RecipeLocal) {
        val recipeList = repoListData.value ?: LinkedList()
        recipeList.removeAll {
            it.id == updatedRecipe.id
        }
        recipeList.add(updatedRecipe)
        repoListData.postValue(recipeList)
    }

    private fun removeFromRecipeList(recipeId: Int) {
        val recipeList = repoListData.value ?: LinkedList()
        recipeList.removeAll {
            it.id == recipeId
        }
        repoListData.postValue(recipeList)
    }

    companion object : SingletonHolder<RecipeRepository, Application>(::RecipeRepository)

}