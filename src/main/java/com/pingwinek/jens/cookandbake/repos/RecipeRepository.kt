package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.MIN_UPDATE_INTERVAL
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class RecipeRepository private constructor(val application: PingwinekCooksApplication) : AuthService.AuthenticationListener {

    private val recipeSourceLocal = application.getServiceLocator().getService(RecipeSourceLocal::class.java)
    private val syncService = application.getServiceLocator().getService(SyncService::class.java)

    private val repoListData = MutableLiveData<LinkedList<Recipe>>()
        .apply {
            value = LinkedList()
    }

    private var lastUpdated: Long = 0

    val recipeListData: LiveData<LinkedList<Recipe>> = repoListData

    init {
        application.getServiceLocator()
            .getService(AuthService::class.java)
            .registerAuthenticationListener(this)
        getAll()
    }

    override fun onLogin() {
        lastUpdated = 0
        getAll()
        Log.i(this::class.java.name, "login")
    }

    override fun onLogout() {
        Log.i(this::class.java.name, "logout")
        clearRecipeList()
    }

    fun checkForUpdates() {
        getAll()
    }

    private fun getAll() {
        val now = Date().time
        if (now - lastUpdated < MIN_UPDATE_INTERVAL) {
            return
        } else {
            lastUpdated = now
        }

        fetchAll()
        syncRecipes {
            fetchAll()
        }
    }

    fun getRecipe(recipeId: Int) {
        // if we already have the recipe, we skip retrieving it again from the local database
        if (recipeListData.value?.find {
                it.id == recipeId
            } == null) {
            fetchRecipe(recipeId)
        }

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
                val newRecipe = result.value
                if (result.status == Promise.Status.SUCCESS && newRecipe != null && confirmUpdate(newRecipe.id)) {
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
        recipeSourceLocal.get(id).setResultHandler { recipeLocalResult ->
            val recipeLocal = recipeLocalResult.value
            if (recipeLocalResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                recipeSourceLocal.update(recipeLocal.getUpdated(title, description, instruction))
                    .setResultHandler{ result ->
                        val updatedRecipe = result.value
                        if (result.status == Promise.Status.SUCCESS && updatedRecipe != null) {
                            updateRecipeList(updatedRecipe)
                            syncRecipe(updatedRecipe.id) {
                                fetchRecipe(updatedRecipe.id)
                            }
                        }
                    }
            }
        }

    }

    fun deleteRecipe(recipeId: Int, callback: () -> Unit) {
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId) {
            recipeSourceLocal.flagAsDeleted(recipeId)
                .setResultHandler { result ->
                    val recipe = result.value
                    if (result.status == Promise.Status.SUCCESS && recipe != null) {
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
                result.value?.let { updateRecipeList(it) }
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

    private fun updateRecipeList(updatedRecipeList: LinkedList<RecipeLocal>) {
        repoListData.value = repoListData.value?.apply {
            clear()
            updatedRecipeList.forEach { updatedRecipe ->
                if (!updatedRecipe.flagAsDeleted) add(updatedRecipe)
            }
            sortBy {
                it.title
            }
        }
    }

    private fun updateRecipeList(updatedRecipe: RecipeLocal) {
        repoListData.value = repoListData.value?.apply {
            val recipe = find {
                it.id == updatedRecipe.id
            }
            if (recipe?.lastModified == updatedRecipe.lastModified) return
            removeAll {
                it.id == updatedRecipe.id
            }
            if (!updatedRecipe.flagAsDeleted) add(updatedRecipe)
            sortBy {
                it.title
            }
        }
    }

    private fun removeFromRecipeList(recipeId: Int) {
        repoListData.value = repoListData.value?.apply {
            removeAll {
                it.id == recipeId
            }
        }
    }

    private fun clearRecipeList() {
        repoListData.value = repoListData.value?.apply {
            clear()
        }
    }

    private fun syncRecipe(recipeId: Int, callback: () -> Unit) {
        syncService.syncEntry<RecipeLocal, RecipeRemote>(recipeId, callback)
    }

    private fun syncRecipes(callback: () -> Unit) {
        syncService.sync<RecipeLocal, RecipeRemote>(callback)
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}