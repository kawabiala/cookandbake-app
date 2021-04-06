package com.pingwinek.jens.cookandbake.repos

import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.MIN_UPDATE_INTERVAL
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.sources.FileManagerRemote
import com.pingwinek.jens.cookandbake.sources.FileManagerRemote.Companion.TYPES
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.FileManagerLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.util.*

class RecipeRepository private constructor(val application: PingwinekCooksApplication) : AuthService.AuthenticationListener {

    private val recipeSourceLocal = application.getServiceLocator().getService(RecipeSourceLocal::class.java)
    private val syncService = application.getServiceLocator().getService(SyncService::class.java)
    private val fileManagerRemote = FileManagerRemote(application)
    private val fileManagerLocal = FileManagerLocal(application)


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

    suspend fun deleteRecipe(recipeId: Int) {
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId)
        val recipe = recipeSourceLocal.flagAsDeleted(recipeId) ?: return
        updateRecipeList(recipe)
        syncRecipe(recipe.id)
        fetchRecipe(recipe.id)
    }

    suspend fun deletePdf(recipeId: Int) {
        val recipe = recipeSourceLocal.get(recipeId) ?: return
        updateRecipe(RecipeLocal(
            recipe.id,
            recipe.remoteId,
            recipe.title,
            recipe.description,
            recipe.instruction
        ))
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

    suspend fun saveFile(recipeId: Int, inputStream: InputStream, type: String) {
        /*
        val name = getFileName(recipeId)
        val localName = name ?: generateLocalName(recipeId, type)
        saveFileLocal(inputStream, localName)
        saveFileRemote(recipeId, inputStream, type, name)

         */
    }

    suspend fun updateRecipe(
        id: Int,
        title: String,
        description: String?,
        instruction: String?
    ) {
        val recipeLocal = recipeSourceLocal.get(id) ?: return
        updateRecipe(recipeLocal.getUpdated(
            title ?: recipeLocal.title,
            description ?: recipeLocal.description,
            instruction ?: recipeLocal.instruction
        )
        )
    }

    private fun clearRecipeList() {
        val updatedList = repoListData.value?.apply {
            clear()
        }
        repoListData.postValue(updatedList)
    }

    private fun deleteFileLocal(name: String) {
        val externalFilesDir = application.applicationContext.getExternalFilesDir(null) ?: return
        val file = File(externalFilesDir, name)
        try {
            file.delete()
        } catch (exception: Exception) {
            Log.i(this::class.java.name, "File for $name could not be deleted due to exception $exception")
        }
    }

    private suspend fun fetchRecipe(recipeId: Int) {
        val recipe = recipeSourceLocal.get(recipeId)
        if (recipe != null) {
            updateRecipeList(recipe)
        } else {
            removeFromRecipeList(recipeId)
        }
    }

    private suspend fun generateLocalName(recipeId: Int, type: String) : String {
        val name = "recipe_local_$recipeId.${TYPES[type]}"
        updateRecipe(recipeId)
        return name
    }

    private suspend fun getFileName(recipeId: Int) : String? {
        TODO()
    }

    private fun removeFromRecipeList(recipeId: Int) {
        val updatedList = repoListData.value?.apply {
            removeAll {
                it.id == recipeId
            }
        }
        repoListData.postValue(updatedList)
    }

    private suspend fun saveFileLocal(inputStream: InputStream, name: String) {
        TODO()
        /*
        val externalFilesDir = application.applicationContext.getExternalFilesDir(null) ?: return
        val file = File(externalFilesDir, name)
        if (externalFilesDir.canWrite() && (!file.exists() || file.canWrite())) {

            withContext(Dispatchers.IO) {
                val fileOutputStream = FileOutputStream(file)

                try {
                    fileOutputStream.write(inputStream.readBytes())
                } catch (ioException: IOException) {
                    Log.e(
                        this::class.java.name,
                        "Could not save file due to exception: $ioException"
                    )
                } finally {
                    fileOutputStream.close()
                }
            }
        }

         */
    }
/*
    private suspend fun saveFileRemote(recipeId: Int, inputStream: InputStream, type: String, name: String?) {
        val uri = fileManagerRemote.saveFile(inputStream, type, name)
        uri?.let {
            updateRecipe(recipeId, it)
        }
    }

 */

    private suspend fun syncRecipe(recipeId: Int) {
        syncService.syncEntry<RecipeLocal, RecipeRemote>(recipeId)
    }

    private suspend fun syncRecipes() {
        syncService.sync<RecipeLocal, RecipeRemote>()
    }

    private suspend fun updateRecipe(
        id: Int
    ) {
        val recipeLocal = recipeSourceLocal.get(id) ?: return
        val updatedRecipe = recipeSourceLocal.update(
            recipeLocal.getUpdated(
                recipeLocal.title,
                recipeLocal.description, recipeLocal.instruction,
                ))
        if (updatedRecipe != null) {
            updateRecipe(updatedRecipe)
        }
    }

    private suspend fun updateRecipe(recipeLocal: RecipeLocal) {
        val updatedRecipe = recipeSourceLocal.update(recipeLocal) ?: return
        updateRecipeList(updatedRecipe)
        syncRecipe(updatedRecipe.id)
        fetchRecipe(updatedRecipe.id)
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

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}