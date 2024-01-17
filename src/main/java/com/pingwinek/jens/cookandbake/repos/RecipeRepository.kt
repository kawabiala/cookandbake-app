package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.io.InputStream
import java.util.LinkedList

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)

    private var lastUpdated: Long = 0

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

    suspend fun delete(recipe: Recipe) {
        recipeSourceFB.delete(recipe as RecipeFB)
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

    suspend fun getAll(): LinkedList<Recipe> {
        return try{
            LinkedList<Recipe>(recipeSourceFB.getAll())
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving recipe list: ${exception.toString()}")
            LinkedList<Recipe>()
        }
    }

    suspend fun get(id: String): Recipe {
        return recipeSourceFB.get(id)
    }

    /*
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

     */

    suspend fun getRecipe(recipeId: String) {
        fetchRecipe(recipeId)
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?
    ) : RecipeFB {
        return recipeSourceFB.new(RecipeFB(title, description, instruction))
    }
/*
    suspend fun saveFile(recipeId: Int, inputStream: InputStream, type: String) {
        val name = getFileName(recipeId)
        val localName = name ?: generateLocalName(recipeId, type)
        saveFileLocal(inputStream, localName)
        saveFileRemote(recipeId, inputStream, type, name)
    }

 */

    suspend fun updateRecipe(
        recipe: Recipe,
        title: String,
        description: String?,
        instruction: String?
    ): Recipe {
        return recipeSourceFB.update(RecipeFB(recipe.id, title, description, instruction))
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
    private suspend fun fetchRecipe(recipeId: String) : Recipe {
        return recipeSourceFB.get(recipeId)
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

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}