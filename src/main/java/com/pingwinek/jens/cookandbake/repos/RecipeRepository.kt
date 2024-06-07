package com.pingwinek.jens.cookandbake.repos

import android.content.Context
import android.net.Uri
import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.UriUtils
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.sources.FileSourceFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.io.File
import java.util.LinkedList

class RecipeRepository private constructor(val application: PingwinekCooksApplication) {

    private val recipeSourceFB = application.getServiceLocator().getService(RecipeSourceFB::class.java)
    private val uriUtils = application.getServiceLocator().getService(UriUtils::class.java)

    suspend fun delete(recipe: Recipe) {
        recipeSourceFB.delete(recipe as RecipeFB)
    }

    suspend fun deleteAttachment(recipe: Recipe): Recipe {
        var returnRecipe = recipe

        try {
            returnRecipe = updateHasAttachment(recipe, false)
            FileSourceFB.deleteFile(getAttachmentFilePath(recipe.id))
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when deleting attachment: $exception")
        }

        return returnRecipe
    }

    suspend fun get(id: String): Recipe {
        return recipeSourceFB.get(id)
    }

    suspend fun getAll(): LinkedList<Recipe> {
        return try{
            LinkedList<Recipe>(recipeSourceFB.getAll())
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving recipe list: $exception")
            LinkedList<Recipe>()
        }
    }

    suspend fun getAttachment(context: Context ,recipe: Recipe): FileInfo? {
        val cacheDir = context.cacheDir
        return try {
            FileSourceFB.getFile(cacheDir, getAttachmentFilePath(recipe.id))
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving attachment: $exception")
            null
        }
    }

    suspend fun newRecipe(
        title: String,
        description: String?,
        instruction: String?
    ) : Recipe {
        return recipeSourceFB.new(RecipeFB(title, description, instruction))
    }

    suspend fun saveAttachment(recipe: Recipe, uri: Uri): Recipe {
        var returnRecipe = recipe

        val name = uriUtils.getNameForUri(uri)
        val size = uriUtils.getSizeForUri(uri)
        val type = uriUtils.getTypeForUri(uri)

        if (name.isNullOrEmpty()) TODO()
        if (size == null || size == 0.toLong() || size > MAX_ATTACHMENT_SIZE) TODO()
        if (type.isNullOrEmpty()) TODO()

        val suffix = File(name).extension
        if (suffix.isEmpty()) TODO()

        // try to delete
        try {
            if (FileSourceFB.deleteFile(getAttachmentFilePath(recipe.id))) {
                returnRecipe = updateHasAttachment(recipe, false)
            }
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when attaching document: $exception")
        }

        // try to upload
        try {
            if (FileSourceFB.uploadFile(getAttachmentFilePath(recipe.id), uri)) {
                returnRecipe = updateHasAttachment(recipe, true)
            }
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when attaching document: $exception")
        }

        return returnRecipe
    }

    private suspend fun updateHasAttachment(recipe: Recipe, hasAttachment: Boolean): Recipe {
        return recipeSourceFB.update(
            RecipeFB(
                recipe.id,
                recipe.title,
                recipe.description,
                recipe.instruction,
                hasAttachment)
        )
    }

    suspend fun updateRecipe(
        recipe: Recipe,
        title: String,
        description: String?,
        instruction: String?
    ): Recipe {
        return recipeSourceFB.update(RecipeFB(recipe.id, title, description, instruction, recipe.hasAttachment))
    }

    companion object : SingletonHolder<RecipeRepository, PingwinekCooksApplication>(::RecipeRepository) {

        const val MAX_ATTACHMENT_SIZE: Long = 1024*1024

        private const val RECIPE_FILE_PATH = "recipe"
        private const val ATTACHMENT_FILE_NAME = "attachment"

        private fun getAttachmentFilePath(id: String): String {
            return "$RECIPE_FILE_PATH/$id/$ATTACHMENT_FILE_NAME"
        }
    }

}