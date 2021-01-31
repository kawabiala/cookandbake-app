package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.MIN_UPDATE_INTERVAL
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.networkRequest.FileManagerRemote
import com.pingwinek.jens.cookandbake.lib.networkRequest.FileManagerRemote.Companion.TYPES
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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
        CoroutineScope(Dispatchers.IO).launch {
            getAll()
        }
    }

    override fun onLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            checkForUpdates(true)
        }
    }

    override fun onLogout() {
        clearRecipeList()
    }

    suspend fun checkForUpdates() {
        checkForUpdates(false)
    }

    suspend fun checkForUpdates(force: Boolean) {
        if (force) {
            lastUpdated = 0
        }
        getAll()
    }

    private suspend fun getAll() {
        val now = Date().time
        if (now - lastUpdated < MIN_UPDATE_INTERVAL) {
            return
        } else {
            lastUpdated = now
        }

        updateRecipeList(recipeSourceLocal.getAll())
        syncRecipes()
        updateRecipeList(recipeSourceLocal.getAll())
    }

    suspend fun getRecipe(recipeId: Int) {
        // if we already have the recipe, we skip retrieving it again from the local database
        if (recipeListData.value?.find {
                it.id == recipeId
            } == null) {
            fetchRecipe(recipeId)
        }

        syncRecipe(recipeId)
        fetchRecipe(recipeId)
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?,
        confirmUpdate: (recipeId: Int) -> Boolean
    ) {
        val newRecipe = recipeSourceLocal.new(RecipeLocal(title, description, instruction, null))
        if (confirmUpdate(newRecipe.id)) {
            updateRecipeList(newRecipe)
            syncRecipe(newRecipe.id)
            fetchRecipe(newRecipe.id)
        }
    }

    suspend fun updateRecipe(
        id: Int,
        title: String,
        description: String?,
        instruction: String?
    ) {
        updateRecipe(id, title, description, instruction, null)
    }

    suspend fun addExternalUri(id: Int, uri: String) {
        updateRecipe(id, null, null, null, uri)
    }

    private suspend fun updateRecipe(
        id: Int, uri: String
    ) {
        val recipeLocal = recipeSourceLocal.get(id) ?: return
        val updatedRecipe = recipeSourceLocal.update(
            recipeLocal.getUpdated(
                recipeLocal.title,
                recipeLocal.description, recipeLocal.instruction,
                uri))
        if (updatedRecipe != null) {
            updateRecipe(updatedRecipe)
        }
    }

    private suspend fun updateRecipe(
        id: Int,
        title: String?,
        description: String?,
        instruction: String?,
        uri: String?
    ) {
        val recipeLocal = recipeSourceLocal.get(id) ?: return
        updateRecipe(recipeLocal.getUpdated(
            title ?: recipeLocal.title,
            description ?: recipeLocal.description,
            instruction ?: recipeLocal.instruction,
            uri ?: recipeLocal.uri)
        )
    }

    private suspend fun updateRecipe(recipeLocal: RecipeLocal) {
        val updatedRecipe = recipeSourceLocal.update(recipeLocal) ?: return
        updateRecipeList(updatedRecipe)
        syncRecipe(updatedRecipe.id)
        fetchRecipe(updatedRecipe.id)
    }

    suspend fun deleteRecipe(recipeId: Int) {
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId)
        val recipe = recipeSourceLocal.flagAsDeleted(recipeId) ?: return
        updateRecipeList(recipe)
        syncRecipe(recipe.id)
        fetchRecipe(recipe.id)
    }

    suspend fun saveFile(recipeId: Int, inputStream: InputStream, type: String) {
        val name = getFileName(recipeId, type)
        val localName = name ?: generateLocalName(recipeId, type)
        saveFileLocal(recipeId, inputStream, localName)
        saveFileRemote(recipeId, inputStream, type, name)
    }

    private suspend fun fetchRecipe(recipeId: Int) {
        val recipe = recipeSourceLocal.get(recipeId)
        if (recipe != null) {
            updateRecipeList(recipe)
        } else {
            removeFromRecipeList(recipeId)
        }
    }

    private fun updateRecipeList(updatedRecipeList: LinkedList<RecipeLocal>) {
        val updatedList = repoListData.value?.apply {
            clear()
            updatedRecipeList.forEach { updatedRecipe ->
                if (!updatedRecipe.flagAsDeleted) add(updatedRecipe)
            }
            sortBy {
                it.title
            }
        }
        repoListData.postValue(updatedList)
    }

    private fun updateRecipeList(updatedRecipe: RecipeLocal) {
        val updatedList = repoListData.value?.apply {
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
        repoListData.postValue(updatedList)
    }

    private fun removeFromRecipeList(recipeId: Int) {
        val updatedList = repoListData.value?.apply {
            removeAll {
                it.id == recipeId
            }
        }
        repoListData.postValue(updatedList)
    }

    private fun clearRecipeList() {
        val updatedList = repoListData.value?.apply {
            clear()
        }
        repoListData.postValue(updatedList)
    }

    private suspend fun saveFileLocal(recipeId: Int, inputStream: InputStream, name: String) {
        val externalFilesDir = application.applicationContext.getExternalFilesDir(null) ?: return
        val file = File(externalFilesDir, name)
        if (!file.canWrite()) return

        withContext(Dispatchers.IO) {
            val fileOutputStream = FileOutputStream(file)

            try {
                fileOutputStream.write(inputStream.readBytes())
            } catch (ioException: IOException) {
                Log.e(this::class.java.name, "Could not save file due to exception: $ioException")
            } finally {
                fileOutputStream.close()
            }
        }
    }

    private suspend fun saveFileRemote(recipeId: Int, inputStream: InputStream, type: String, name: String?) {
        val fileManagerRemote = FileManagerRemote(application)
        val uri = fileManagerRemote.saveFile(inputStream, type, name)
        uri?.let {
            updateRecipe(recipeId, it)
        }
    }

    private suspend fun getFileName(recipeId: Int, type: String) : String? {
        return recipeSourceLocal.get(recipeId)?.uri
    }

    private suspend fun generateLocalName(recipeId: Int, type: String) : String {
        val name = "recipe_local_$recipeId.${TYPES[type]}"
        updateRecipe(recipeId, name)
        return name
    }

    private suspend fun syncRecipe(recipeId: Int) {
        syncService.syncEntry<RecipeLocal, RecipeRemote>(recipeId)
    }

    private suspend fun syncRecipes() {
        syncService.sync<RecipeLocal, RecipeRemote>()
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}