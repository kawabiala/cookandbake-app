package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)

    suspend fun delete(recipe: Recipe) {
        recipeSourceFB.delete(recipe as RecipeFB)
    }

    suspend fun getAll(): LinkedList<Recipe> {
        return try{
            LinkedList<Recipe>(recipeSourceFB.getAll())
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving recipe list: $exception")
            LinkedList<Recipe>()
        }
    }

    suspend fun get(id: String): Recipe {
        return recipeSourceFB.get(id)
    }


    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?
    ) : RecipeFB {
        return recipeSourceFB.new(RecipeFB(title, description, instruction))
    }

    suspend fun updateRecipe(
        recipe: Recipe,
        title: String,
        description: String?,
        instruction: String?
    ): Recipe {
        return recipeSourceFB.update(RecipeFB(recipe.id, title, description, instruction))
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

        suspend fun saveFile(recipeId: Int, inputStream: InputStream, type: String) {
            val name = getFileName(recipeId)
            val localName = name ?: generateLocalName(recipeId, type)
            saveFileLocal(inputStream, localName)
            saveFileRemote(recipeId, inputStream, type, name)
        }

        private suspend fun generateLocalName(recipeId: Int, type: String) : String {
            val name = "recipe_local_$recipeId.${TYPES[type]}"
            updateRecipe(recipeId)
            return name
        }

        private suspend fun getFileName(recipeId: Int) : String? {
            TODO()
        }

    private suspend fun saveFileLocal(inputStream: InputStream, name: String) {
        TODO()
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
    }

    private suspend fun saveFileRemote(recipeId: Int, inputStream: InputStream, type: String, name: String?) {
        val uri = fileManagerRemote.saveFile(inputStream, type, name)
        uri?.let {
            updateRecipe(recipeId, it)
        }
    }

 */

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository)

}