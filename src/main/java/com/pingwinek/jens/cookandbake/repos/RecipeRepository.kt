package com.pingwinek.jens.cookandbake.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    private lateinit var recipeSourceLocal: RecipeSourceLocal
    private lateinit var syncService: SyncService

    init {
        application.getServiceLocator().getService(RecipeSourceLocal::class.java)?.also {
            recipeSourceLocal = it
        } ?: throw NullPointerException("application.getServiceLocator().getService(RecipeSourceLocal::class.java) returns null")
        application.getServiceLocator().getService(SyncService::class.java)?.also {
            syncService = it
        } ?: throw NullPointerException("application.getServiceLocator().getService(SyncService::class.java) returns null")
    }

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
        syncRecipes {
            fetchAll()
        }
    }

    fun getRecipe(recipeId: Int) {
        fetchRecipe(recipeId)
        syncRecipe(recipeId) {
            fetchRecipe(recipeId)
        }
    }

    fun newRecipe(
        title: String,
        description: String?,
        instruction: String?,
        confirmUpdate: (recipeId: Int) -> Boolean
    ) {
        recipeSourceLocal.new(RecipeLocal(title, description, instruction))
            .setResultHandler{ result ->
                val status = result.status
                val newRecipe = result.value
                if (status == Promise.Status.SUCCESS && newRecipe != null && confirmUpdate(newRecipe.id)) {
                    updateRecipeList(newRecipe)
                    syncRecipe(newRecipe.id) {
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

        recipeSourceLocal.update(recipe.getUpdated(title, description, instruction))
            .setResultHandler{ result ->
                val status = result.status
                val updatedRecipe = result.value
                if (status == Promise.Status.SUCCESS && updatedRecipe != null) {
                    updateRecipeList(updatedRecipe)
                    syncRecipe(updatedRecipe.id) {
                        fetchRecipe(updatedRecipe.id)
                    }
                }
        }
    }

    fun deleteRecipe(recipeId: Int, callback: () -> Unit) {
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId) {
            recipeSourceLocal.flagAsDeleted(recipeId)
                .setResultHandler { result ->
                    val status = result.status
                    val recipe = result.value
                    if (status == Promise.Status.SUCCESS && recipe != null) {
                        updateRecipeList(recipe)
                        syncRecipe(recipe.id) {
                            fetchRecipe(recipe.id)
                            callback()
                        }
                    }
            }
        }
    }

    private fun fetchAll() {
        recipeSourceLocal.getAll()
            .setResultHandler { result ->
                repoListData.postValue(result.value)
        }
    }

    private fun fetchRecipe(recipeId: Int) {
        recipeSourceLocal.get(recipeId)
            .setResultHandler{ result ->
                val status = result.status
                val recipe = result.value
                if (status == Promise.Status.SUCCESS && recipe != null) {
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

    private fun syncRecipe(recipeId: Int, callback: () -> Unit) {
        syncService.syncEntry<RecipeLocal, RecipeRemote>(recipeId, callback)
    }

    private fun syncRecipes(callback: () -> Unit) {
        syncService.sync<RecipeLocal, RecipeRemote>(callback)
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}