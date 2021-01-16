package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.MIN_UPDATE_INTERVAL
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.networkRequest.FileManagerRemote
import com.pingwinek.jens.cookandbake.lib.networkRequest.FileManagerRemote.Companion.TYPES
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
        getAll()
    }

    override fun onLogin() {
        checkForUpdates(true)
        Log.i(this::class.java.name, "login")
    }

    override fun onLogout() {
        Log.i(this::class.java.name, "logout")
        clearRecipeList()
    }

    fun checkForUpdates() {
        checkForUpdates(false)
    }

    fun checkForUpdates(force: Boolean) {
        if (force) {
            lastUpdated = 0
        }
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
        recipeSourceLocal.new(RecipeLocal(title, description, instruction, null))
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
        updateRecipe(id, title, description, instruction, null)
    }

    fun addExternalUri(id: Int, uri: String) {
        updateRecipe(id, null, null, null, uri)
    }

    private fun updateRecipe(
        id: Int,
        title: String?,
        description: String?,
        instruction: String?,
        uri: String?
    ) {
        recipeSourceLocal.get(id).setResultHandler { recipeLocalResult ->
            val recipeLocal = recipeLocalResult.value
            if (recipeLocalResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                recipeSourceLocal.update(
                    recipeLocal.getUpdated(
                        title ?: recipeLocal.title,
                        description ?: recipeLocal.description,
                        instruction ?: recipeLocal.instruction,
                        uri ?: recipeLocal.uri))
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

    private fun updateRecipe(recipeId: Int, uri: String) {
        recipeSourceLocal.get(recipeId).setResultHandler { recipeLocalResult ->
            val recipeLocal = recipeLocalResult.value
            if (recipeLocalResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                recipeSourceLocal.update(
                    recipeLocal.getUpdated(
                        recipeLocal.title,
                        recipeLocal.description,
                        recipeLocal.instruction,
                        uri))
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

    fun saveFile(recipeId: Int, inputStream: InputStream, type: String) {
        getFileName(recipeId, type).setResultHandler { result ->
            if (result.status == Promise.Status.FAILURE) return@setResultHandler
            val name = result.value
            val localName = name ?: generateLocalName(recipeId, type)
            runBlocking {
                saveFileLocal(recipeId, inputStream, localName)
            }
            saveFileRemote(recipeId, inputStream, type, name)
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

    private fun saveFileRemote(recipeId: Int, inputStream: InputStream, type: String, name: String?) {
        val fileManagerRemote = FileManagerRemote(application)
        fileManagerRemote.saveFile(inputStream, type, name)
            .setResultHandler { result ->
                if (result.status == Promise.Status.SUCCESS && name == null && result.value != null) {
                    updateRecipe(recipeId, result.value)
                }
            }
    }

    private fun getFileName(recipeId: Int, type: String) : Promise<String> {
        val promise = Promise<String>()
        var name: String? = null

        recipeSourceLocal.get(recipeId)
            .setResultHandler { result ->
                promise.setResult(result.status, result.value?.uri)
            }

        return promise
    }

    private fun generateLocalName(recipeId: Int, type: String) : String {
        val name = "recipe_local_$recipeId.${FileManagerRemote.TYPES[type]}"
        updateRecipe(recipeId, name)
        return name
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}