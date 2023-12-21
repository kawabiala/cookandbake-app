package com.pingwinek.jens.cookandbake.repos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.LinkedList

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    /*
    private val fileManagerRemote = FileManagerRemote(application)
    private val fileManagerLocal = FileManagerLocal(application)

     */

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)

    private val repoListData = MutableLiveData<LinkedList<RecipeFB>>().apply {
        value = LinkedList<RecipeFB>()
    }
    val recipeListData = repoListData.map() {
        LinkedList<Recipe>().apply {
            it.forEach { recipeFB ->
                add(recipeFB)
            }
        }
    }

    private var lastUpdated: Long = 0

    //val recipeListData: LiveData<LinkedList<Recipe>> = repoListData

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getAll()
        }
    }

    /* TODO: Check for login and logout
    override fun onLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            checkForUpdates(true)
        }
    }

    override fun onLogout() {
        clearRecipeList()
    }

     */

    suspend fun checkForUpdates() {
        checkForUpdates(false)
    }

    suspend fun checkForUpdates(force: Boolean) {
        if (force) {
            lastUpdated = 0
        }
        getAll()
    }

    suspend fun deleteRecipe(recipeId: String) {
        val recipeFB = repoListData.value?.find {
            it.id == recipeId
        } ?: return
        IngredientRepository.getInstance(application).deleteIngredientForRecipeId(recipeId)
        deleteRecipe(recipeFB)
        getAll()
    }
/*
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

 */

    private suspend fun getAll() {
        /*
        val now = Date().time
        if (now - lastUpdated < MIN_UPDATE_INTERVAL) {
            return
        } else {
            lastUpdated = now
        }

         */
        //Log.i(this::class.java.name, "getAll")
        updateRecipeList(recipeSourceFB.getAll())
    }

    suspend fun getRecipe(recipeId: String) {
        fetchRecipe(recipeId)
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?
    ) : RecipeFB? {
        val newRecipe = recipeSourceFB.new(RecipeFB(title, description, instruction))
        newRecipe?.let { updateRecipeList(it) }
        return newRecipe
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
        id: String,
        title: String,
        description: String?,
        instruction: String?
    ) {
        val recipeToUpdate = RecipeFB(id, title, description, instruction)
        updateRecipe(recipeToUpdate)
    }

    private fun clearRecipeList() {
        val updatedList = repoListData.value?.apply {
            clear()
        }
        repoListData.postValue(updatedList)
    }
/*
    private fun deleteFileLocal(name: String) {
        val externalFilesDir = application.applicationContext.getExternalFilesDir(null) ?: return
        val file = File(externalFilesDir, name)
        try {
            file.delete()
        } catch (exception: Exception) {
            Log.i(this::class.java.name, "File for $name could not be deleted due to exception $exception")
        }
    }
*/
    private suspend fun fetchRecipe(recipeId: String) {
        val recipe = recipeSourceFB.get(recipeId)
        if (recipe != null) {
            updateRecipeList(recipe)
        } else {
            removeFromRecipeList(recipeId)
        }
    }
/*
    private suspend fun generateLocalName(recipeId: Int, type: String) : String {
        val name = "recipe_local_$recipeId.${TYPES[type]}"
        updateRecipe(recipeId)
        return name
    }

    private suspend fun getFileName(recipeId: Int) : String? {
        TODO()
    }

*/
    private suspend fun deleteRecipe(recipeFB: RecipeFB) : Boolean {
        val result = recipeSourceFB.delete(recipeFB)
        removeFromRecipeList(recipeFB.id)
        return result
    }

    private fun removeFromRecipeList(recipeId: String) {
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

 /*
    private suspend fun updateRecipe(
        id: String
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

  */

    private suspend fun updateRecipe(recipe: RecipeFB) {
        val updatedRecipe = recipeSourceFB.update(recipe) ?: return
        updateRecipeList(updatedRecipe)
    }

    private fun updateRecipeList(updatedRecipe: RecipeFB) {
        val updatedList = repoListData.value?.apply {
            val recipe = find {
                it.id == updatedRecipe.id
            }
            //if (recipe?.lastModified == updatedRecipe.lastModified) return
            removeAll {
                it.id == updatedRecipe.id
            }
            add(updatedRecipe)
            sortBy {
                it.title
            }
        }
        repoListData.postValue(updatedList)
    }

    private fun updateRecipeList(updatedRecipeList: LinkedList<RecipeFB>) {
        val updatedList = repoListData.value?.apply {
            clear()
            updatedRecipeList.forEach { updatedRecipe ->
                add(updatedRecipe)
            }
            sortBy {
                it.title
            }
        }
        repoListData.postValue(updatedList)
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}